/***************************************************************************
 * Thomson, Daniel R.     ECE 531 Summer 2024
 *   
 * Thermostat app 
 *	
 * DESCRIPTION: An IoT client that communicates with the cloud to relay data
 *              and respond to programming
 *   
 * OUTPUT: 
 *  [] local    /var/log/messages
 *  [] local    /tmp/status
 *  [] remote   aws-ec2-server
***************************************************************************/

#include <stdio.h>
#include <syslog.h>     //For syslog()
#include <signal.h>     //For SIGHUP, SIGTERM
#include <stdbool.h>    //For boolean logic
#include <unistd.h>     //For sleep() and fork()
#include <errno.h>      //For errno
#include <string.h>     //For strerror()
#include <sys/stat.h>   //For Umask
#include <sys/types.h>  
#include <stdlib.h>
#include <curl/curl.h>  // for curl 
#include <argp.h>       // for argument parser

#include "thermostat.h"

/**
 * Map an error code to a string.
 * NOTE: Leveraged from thermocouple
 *
 * @param err The error code.
 * @return A string related to the error code.
 */
const char* error_to_msg(const int err) 
{
  char* msg = NULL;
  switch(err) 
  {
    case OK:
      msg = "Everything is just fine.";
      break;
    case NO_FORK:
      msg = "Unable to fork a child process.";
      break;
    case NO_SETSID:
      msg = "Unable to set the session id.";
      break;
    case RECV_SIGTERM:
    case RECV_SIGSTOP:
      msg = "Received a termination signal; exiting.";
      break;
    case RECV_SIGKILL:
      msg = "Received a kill signal; exiting.";
      break;
    case REQ_ERR:
      msg = "Requested resources is unavailable.";
      break;
    case NO_FILE:
      msg = "File not found/opened.";
      break;
    case INIT_ERR:
      msg = "Unable to initialze object.";
      break;
    case ERR_CHDIR:
      msg = "Unable to change directories.";
      break;
    case WEIRD_EXIT:
    case ERR_WTF:
      msg = "An unexptected condition has come up, exiting.";
      break;
    case UNKNOWN_HEATER_STATE:
      msg = "Encountered an unknown heater state!";
      break;
    case FLAME_ON:
      msg = "Turning heater on.";
      break;
    case ICE_ICE_BABY:
      msg = "Turning heater off.";
      break;
    default:
      msg = "You submitted some kind of wackadoodle error code. What's up with you?";
  }
  return msg;
}

/*
 * Exit the process in a civilized manner
 * NOTE: Leveraged from thermocouple
 * 
 * @param err: The error code
 */
static void _exit_process(const int err)
{
  syslog(LOG_INFO, "%s", error_to_msg(err));
  closelog(); 
  exit(err);
}

/*
 * Curl call_back function for retrieving data
 * @param data The resultant data
 * @param size The size of the resultant data
 * @param nmemb The block size in bytes
 * @param userp The raw curl buffer
 */
static size_t call_back(void *data, size_t size, size_t nmemb, void *userp)
{
    size_t realsize = size * nmemb;
    struct CurlBuffer *buf = (struct CurlBuffer *)userp;

    char *ptr = realloc(buf->response, buf->size + realsize + 1);
    if (ptr == NULL)
    {
        syslog(LOG_INFO, "response ptr is null");
        return 0;
    }

    buf->response = ptr;
    memcpy(&(buf->response[buf->size]), data, realsize);
    buf->size += realsize;
    buf->response[buf->size] = 0;

    return realsize;
}

/*
 * Curl buffer object
 */
struct CurlBuffer chunk = {0};

/*
 * A helper function to use curl.h to send and ack curl requests
 * @param url The fully qualified url
 * @param message What we want to sent to the url
 * @param type {Post, PUT, GET, DELETE}
 * @param hasVerb A way for me to know if there's an additional piece of work I need to do
 * @return The response message (if any)
 */
static char * doCurlAction(const char *url, char *message, char *type, bool hasVerb)
{
    CURL *curl = curl_easy_init();
    if (curl)
    {
	CURLcode rCurlCode;
        FILE* outFile = fopen("curlCache.txt", "wb");
    
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, type);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, outFile);

        if (strcmp(type, "GET") == 0)
        {
	    syslog(LOG_INFO, "Handling GET cmd");
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, call_back);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
        }

        if (hasVerb)
        {
	    syslog(LOG_INFO, "Handling verb based cmd");
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, message);
        }
        else
        {
            curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
        }
	
	rCurlCode = curl_easy_perform(curl);
        
	if (rCurlCode != CURLE_OK)
        {
            syslog(LOG_INFO, "Bad curl response.");
            return (char *) REQ_ERR;
        }

        curl_easy_cleanup(curl);
    }
    else
    {
        syslog(LOG_INFO, "Unable to initialze curl driver.");
        return (char *) INIT_ERR;
    }
    syslog(LOG_INFO, "Response (if any): %s", chunk.response);
    return chunk.response;
}

/*
 *  argp parser
 *  NOTE: see online for argp docs
 */
static error_t parser(int key, char *arg, struct argp_state *state)
{
    struct Arguments *arg_struct = state->input;
    switch (key)
    {
        case 'u':
            arg_struct->url = arg;
            break;
	case 'c':
	    arg_struct->configDir = arg;
        case 'o':
            arg_struct->post = true;
            break;
        case 'g':
            arg_struct->get = true;
            break;
        case 'p':
            arg_struct->put = true;
            break;
        case 'd':
            arg_struct->delete = true;
            break;
        case ARGP_KEY_NO_ARGS: // Args missing
            if (arg_struct->post == true
                    || arg_struct->put == true
                    || arg_struct->delete == true)
            {
                syslog(LOG_INFO, "Verbs are missing from argument structure.");
                argp_usage(state);
                return REQ_ERR;
            }
        case ARGP_KEY_ARG:
            if (state->arg_num >= 1)
            {
                syslog(LOG_INFO, "Too many arguments, use quotes around your extra argument.");
                argp_usage(state);
                return REQ_ERR;
            }

            arg_struct->msg = arg;
            
	    break;
        case ARGP_KEY_END: // Reached the last key, check input.
            if (arg_struct->url == NULL)
            {
                syslog(LOG_INFO, "Invalide URL provided.");
                argp_usage(state);
                return REQ_ERR;
            }
            else if (arg_struct->get == false
                    && arg_struct->post == false
                    && arg_struct->put == false
                    && arg_struct->delete == false)
            {
                syslog(LOG_INFO, "http request type missing.");
                argp_usage(state);
                return REQ_ERR;
            }
	    else if (arg_struct->configDir == NULL)
	    {
	       syslog(LOG_INFO, "No config directory provided using default directory.");
	    }
            break;
        case ARGP_KEY_SUCCESS: // perform request
            if (arg_struct->get) 
            {
	    	doCurlAction(arg_struct->url, NULL, "GET", false);
                break;
            }
            else if (arg_struct->post)
            {
		 doCurlAction(arg_struct->url, arg_struct->msg, "POST", true);
                 break;
            }
            else if (arg_struct->put)
            {
                doCurlAction(arg_struct->url, arg_struct->msg, "PUT", true);
                break;
            }
            else if (arg_struct->delete)
            {
		doCurlAction(arg_struct->url, arg_struct->msg, "DELETE", true);
                break;
            }
        default:
            return ARGP_ERR_UNKNOWN;
    }
    return OK;
}

/*
 * DISCLAIMER: I had to use argp for this program which is a breaking change from
 * my original implemention and consequently I do owe a great deal of the success
 * of this project to the internet.
 */
static struct argp argp = {options, parser, usage_help, description, 0, 0, 0};

/*
 * Read temperature file and publish the data to the database
 */
static void publishMeasurement(void)
{
    char *buffer = NULL;
    size_t size = 0;

    FILE *fp = fopen(TEMP_PATH, "r");
    fseek(fp, 0, SEEK_END);
    size = ftell(fp);
    rewind(fp);
    buffer = malloc((size + 1) * sizeof(*buffer)); 
    fread(buffer, size, 1, fp);
    buffer[size] = '\0';
    
    syslog(LOG_INFO, "%s:%s", "Temperature taken successfully", buffer);
    doCurlAction(MEAS_TBL_URL, buffer, "POST", true);
}

/*
 * Code to manage the heater
 * @param state Should the heater be on or off based on the programming?
 * @return Messaging for syslog
 */
static int setHeater(char *state)
{
    FILE *fp = fopen(STAT_PATH, "w");
    if (fp == NULL)
    {
        return UNKNOWN_HEATER_STATE;
    }

    fputs(state, fp);
    fclose(fp);

    if (strcmp(state, "ON") == 0)
    {
        return FLAME_ON;
    }
    return ICE_ICE_BABY;
}

/*
 * Handle curl request to know if system should be on or off
 */
static void requestStatus(void)
{
    int code = OK;
    syslog(LOG_INFO, "Requesting status");
    const char *status = doCurlAction(STATUS_TBL_URL, NULL, "GET", false);
   
    // get state from status
    char *state_keyword = "\"state\":";
    char *start, *end;

    start = strstr(status, state_keyword);
    if (start)
    {
        start += strlen(state_keyword);
        end = strchr(start, ',');
        if (end)
       	{
            *end = '\0';
        }
    }

    if (strcmp(start, "true") == 0) 
    {
       code = setHeater("ON");
    }
    else 
    {
       code = setHeater("OFF");
    }

    syslog(LOG_INFO, "%s", error_to_msg(code));

    chunk.response = NULL;
    chunk.size = 0;
}

/*
 * Helper function to check if a file exists
 * @param fname Filename
 * @return yes or no
 */
static bool file_exists(const char* fname)
{
    struct stat buffer;
    return (stat(fname, &buffer) == 0) ? true : false;
}

/*
 * Publish the configuration data to the database
 * @param config The configuration data. Currently only
 * 	3 configurations are supported
 */
static void publishConfiguration(const char* config)
{
    char *buffer = NULL;
    size_t size = 0;

    FILE *fp = fopen(config, "r");
    fseek(fp, 0, SEEK_END);
    size = ftell(fp);
    rewind(fp);
    buffer = malloc((size + 1) * sizeof(*buffer)); 
    fread(buffer, size, 1, fp);
    buffer[size] = '\0';
    
    syslog(LOG_INFO, "%s: %s", "Config parsed successfully", buffer);
    doCurlAction(CONFIG_TBL_URL, buffer, "POST", true);
    sleep(1);
}

/*
 * Helper function to combine c strings
 * @param str1 String one
 * @param str2 String two
 * @return Combination or str1 and str2
 */
static char* combine(const char* str1, const char* str2)
{
    char* result = malloc(strlen(str1) + strlen(str2) + 1);
    strcpy(result, str1);
    strcat(result, str2);
    return result;
}

/*
 * Read and error handle if necissary all the programming files
 * then ensure that the data is sent to the database
 * @param configDir The directory where the configuration files are stored
 */
static void setConfiguration(const char* configDir)
{
    char* cFile1 = combine(configDir, WKDAY_MOR_CONF);
    if (file_exists(cFile1))
    {
       publishConfiguration(cFile1);
    }
    free(cFile1);

    char* cFile2 = combine(configDir, WKDAY_MID_CONF);
    if (file_exists(cFile2))
    {
        publishConfiguration(cFile2);
    }
    free(cFile2);

    char* cFile3 = combine(configDir, WKDAY_NIG_CONF);
    if (file_exists(cFile3))
    {
        publishConfiguration(cFile3);
    }
    free(cFile3);
}

/*
 * Signal handling code block for forked daemonized code
 * @param the signal identifier
 */
static void _signal_handler(const int signal)
{
    switch (signal)
    {
        case SIGHUP:
            break;
        case SIGTERM:
            _exit_process(RECV_SIGTERM);
            break;
        case SIGSTOP:
            _exit_process(RECV_SIGSTOP);
            break;
        case SIGKILL:
            _exit_process(RECV_SIGKILL);
            break;
        default:
            syslog(LOG_INFO, "received unhandled signal");
	    break;
    }
}

/*
 * The engine of the thermostat program
 * @return helpful debug messages
 */
static int execute(void)
{
    // check operation of "tcsimd"
    if (file_exists(TEMP_PATH) && file_exists(STAT_PATH))
    {
        syslog(LOG_INFO, "Thermocouple test passed.");

        while (true)
        {
       		// Read temp and send post to webserver for thermostat
		publishMeasurement();
       		// Get and respond to the heater status
                requestStatus();
                // Rinse and repeat and nearly the cadence of the simulator
		sleep(4);
        }
        return WEIRD_EXIT;
    }
        syslog(LOG_ERR, "Thermocouple test failed.");
        return NO_FILE;
}

/*
 * Program genesis
 */
int main(int argc, char **argv)
{
    if (argc > 1)
    {
        syslog(LOG_INFO, "-Using CLI-");
   
        // initialize arguments
        struct Arguments arg_struct;
        arg_struct.configDir	= NULL;
        arg_struct.url 		= NULL;
	arg_struct.msg 		= NULL;
        arg_struct.post 	= false;
        arg_struct.get 		= false;
        arg_struct.put 		= false;
        arg_struct.delete 	= false;
	
        // parse arguments
        argp_parse(&argp, argc, argv, 0, 0, &arg_struct);

	// set configuration based on input parameter
	setConfiguration(arg_struct.configDir);
    }
    else
    {
        syslog(LOG_INFO, "-Using daemon-");
	    
	openlog(DAEMON_NAME, LOG_PID | LOG_NDELAY | LOG_NOWAIT, LOG_DAEMON);

        syslog(LOG_INFO, "%s: %s", "Starting daemon", DAEMON_NAME);

        pid_t pid = fork();

        // exit if fork fails
        if (pid < 0)
        {
            _exit_process(NO_FORK);
        }

        // check that parent fork returns pid of child
        if (pid > 0)
        {
            return OK;
        }

        // creates a new session as process group leader
        pid_t new_pid = setsid();
        if (new_pid < 0)
        {
            _exit_process(NO_SETSID);
        }

        // Close unused pointers
        close(STDIN_FILENO);
        close(STDOUT_FILENO);
        close(STDERR_FILENO);

        //Mask file permissions R/W only for the user
        umask(S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);

        // change working directory
        if (chdir("/") < 0)
        {
            _exit_process(ERR_CHDIR);
        }

        // handle signals
        signal(SIGTERM, _signal_handler);
        signal(SIGHUP, _signal_handler);
        signal(SIGSTOP, _signal_handler);
        signal(SIGKILL, _signal_handler);
        
        // use the config files found in the home directory
        setConfiguration(DFT_CONF_DIR);
    }
    
    int code = execute();
    if (code != OK)
    {
        _exit_process(code);
    }

    return OK;
}

