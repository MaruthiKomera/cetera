Manage Users
================

The purpose of this functionality is to provide APIs to create internal or external advisors.

Detailed Process Flow
---------------------
* Internal Advisors
** Internal advisors click the banner in cetera website and cetera will generate encrypted payload and post to DOL website.
** DOL front-end will POST encrypted payload through API call.
** Back-end will decrypt payload, compare advisor id with DB.
** If advisor is in DB, check if there is answers created for that person, and if there is active session for that person, and return a new session or renew the current session.
** If advisor is not in DB, return a new session.

* External Advisors
** External advisors go to main entry point of DOL website, and enter revenue and personal info.
** Back-end check if advisor info is already in DB.
** If advisor is in DB, return a new session.
** If advisor is not in DB, create person record first and return a new session.


API Details
-----------

### create new external advisor ###
    /api/people
    @RequestMapping(method = RequestMethod.PUT)
    public Sessions createExternal(@RequestHeader("X-CS-Auth") String auth,
                                   @RequestBody PersonRegisterRequest request)

#### create new internal advisor ####
    /api/people
    @RequestMapping(method = RequestMethod.PUT)
        public Sessions createInternal(@RequestHeader("X-CS-Auth") String auth,
                                       @RequestBody String encryptedPayload)

Request and Response Models
-----------
PersonRegisterRequest {
    Person person;
    Revenue revenue;
}

Person {
    String id;
    String firstName,
    String lastName;
    String email;
    String consultantEmail;
    String brokerDealer;
}

Revenue {
    Long id;
    String personId;
    BigDecimal yearlyRevenue;
    BigDecimal qualifiedPerc;
    BigDecimal nonRecurringPerc;
}