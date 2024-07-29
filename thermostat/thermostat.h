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
#define RECV_SIGSTOP		12
#define FLAME_ON		13
#define ICE_ICE_BABY		14

#define DAEMON_NAME     "thermostatd"

// constants
static const char* CONFIG_TBL_URL   = "http://3.132.111.9:8080/config_tbl";
static const char* STATUS_TBL_URL   = "http://3.132.111.9:8080/status_tbl";
static const char* MEAS_TBL_URL     = "http://3.132.111.9:8080/meas_tbl";
static const char* TEMP_PATH        = "/tmp/temp";
static const char* STAT_PATH        = "/tmp/status";
static const char* DFT_CONF_DIR     = "/thermostat/";
static const char* WKDAY_MOR_CONF   = "weekdayMorningConfig.txt";
static const char* WKDAY_MID_CONF   = "weekdayMiddayConfig.txt";
static const char* WKDAY_NIG_CONF   = "weekdayNightConfig.txt";

// Arguments for serving CLI
struct Arguments
{
    char *configDir;
    char *url;
    char *msg;
    bool post;
    bool get;
    bool put;
    bool delete;
};

// argp constant - usage script
static char usage_help[] = "-c /<directory>/ --url http://3.132.111.9:8080/status_tbl -g";

// argp constant - documentation
static char description[] = "\n\t\t\t>++('> >++('> >++('>\n\
\nProvide a url and perform a GET, POST, DELETE or PUT operation.";

// argp options
static struct argp_option options[] =
{
    {"url",     'u',    "String",  NONE,    "Required. Takes a properly formated URL followed by a port number", NONE},
    {"post",    'o',    NONE,      NONE,    "Requires verb. Perform an HTTP POST.", NONE},
    {"get",     'g',    NONE,      NONE,    "Does not require verb. Perform an HTTP GET.", NONE},
    {"put",     'p',    NONE,      NONE,    "Requires verb. Perform an HTTP PUT.", NONE},
    {"delete",  'd',    NONE,      NONE,    "Requires verb. Perform an HTTP DELETE", NONE},    
    {"config",  'c',    "String",  NONE,    "Not required. Directory where config files are stored", NONE},
    {NONE,      NONE,   NONE,      NONE,    NONE,   NONE}
};

// A holder for curl responses
struct CurlBuffer
{
   char *response;
   size_t size;
 };
 
#endif

