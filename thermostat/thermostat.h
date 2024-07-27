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
static char usage[] = 
    "Usage:\
\n-u/--url\tTakes a properly formated URL followed by a port number\
\n-o/--post\tPerform an HTTP POST\
\n-g/--get\tPerform an HTTP GET\
\n-p/--put\tPerform an HTTP PUT\
\n-d/--delete\tPerform an HTTP DELETE\
\n-h/--help\tShow this usage message\
\nExample:\t./thermosatd --url http://3.132.111.9:8080/status_tbl> -g\n";

// argp constant - documentation
static char description[] =
    "Provide a url and perform a GET, POST, DELETE or PUT operation.";

// argp options
static struct argp_option opt[] =
{
    {"url",     'u', "String",  NONE, "Required"},
    {"post",    'o', NONE,      NONE, "Requires verb"},
    {"get",     'g', NONE,      NONE, "Does not require verb"},
    {"put",     'p', NONE,      NONE, "Requires verb"},
    {"delete",  'd', NONE,      NONE, "Requires verb"},
    {NONE}
};

// A holder for curl responses
struct CurlBuffer
{
   char *response;
   size_t size;
 };
 
#endif

