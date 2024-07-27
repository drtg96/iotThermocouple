/*******************************************************************************
 * Thermostat application headerfile
 ******************************************************************************/

#ifndef __THERMOSTAT_H__
#define __THERMOSTAT_H__

// System keywords
#define OK                      0
#define NONE                    0
#define NO_SETSID               1
#define NO_FORK                 2
#define ERR_CHDIR               3
#define ERR_WTF                 4
#define INIT_ERR                5
#define REQ_ERR                 6
#define NO_FILE                 7
#define RECV_SIGTERM            8
#define RECV_SIGKILL            9
#define WEIRD_EXIT              10
#define UNKNOWN_HEATER_STATE    11
#define DAEMON_NAME     "thermostatd"

// constants
static const char* CONFIG_TBL_URL   = "http://3.132.111.9:8080/config_tbl";
static const char* STATUS_TBL_URL   = "http://3.132.111.9:8080/status_tbl";
static const char* MEAS_TBL_URL     = "http://3.132.111.9:8080/meas_tbl";
static const char* TEMP_PATH        = "/tmp/temp";
static const char* STAT_PATH        = "/tmp/status";

// Arguments for serving CLI
struct Arguments
{
    char *arg;
    char *url;
    bool post;
    bool get;
    bool put;
    bool delete;
};

// argp constant - usage script
static char example[] = "--url http://3.132.111.9:8080/status_tbl> -g";

// argp constant - documentation
static char description[] =
"Provide a url and perform a GET, POST, DELETE or PUT operation.\
      __________\
     | _  ____  """"---,,,______________________\
     |(_)|37.2|           ____________________|_)\
DRT  |__________,,,---""";

// argp options
static struct argp_option options[] =
{
    {"url",     'u',    "String",  NONE,    "Required. Takes a properly formated URL followed by a port number", NONE},
    {"post",    'o',    NONE,      NONE,    "Requires verb. Perform an HTTP POST.", NONE},
    {"get",     'g',    NONE,      NONE,    "Does not require verb. Perform an HTTP GET.", NONE},
    {"put",     'p',    NONE,      NONE,    "Requires verb. Perform an HTTP PUT.", NONE},
    {"delete",  'd',    NONE,      NONE,    "Requires verb. Perform an HTTP DELETE", NONE},
    {"help",    'h',    NONE,      NONE,    "Show this usage message.", NONE},
    {NONE,      NONE,   NONE,      NONE,    NONE,   NONE}
};

// A holder for curl responses
struct CurlBuffer
{
   char *response;
   size_t size;
 };
 
#endif

