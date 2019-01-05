Manage Sessions
================

The purpose of this functionality is to provide APIs to create, delete and renew sessions.

Business Requirements
---------------------
When front-end call api/people PUT, back-end should create or renew the current session;
Front-end can logout the current session or trigger renewing the current session.

API Details
-----------

### logout current session ###
    @RequestMapping(value = "/logout", method = RequestMethod.PATCH)
    public void logout(@RequestHeader("X-CS-Auth") String auth,
                       @RequestHeader("X-CS-Session") String sessionId)

### retrieve the current session ###
    @RequestMapping(method = RequestMethod.GET)
    public Sessions retrieve(@RequestHeader("X-CS-Auth") String auth,
                             @RequestHeader("X-CS-Session") String sessionId)
### renew the current session ###
    public void renew(@RequestHeader("X-CS-Auth") String auth,
                      @RequestHeader("X-CS-Session") String sessionId)

Request and Response Models
-----------

Sessions {
    String id;
    String personId;
    Date expiresOn;
    String resourceAvailable;
}
