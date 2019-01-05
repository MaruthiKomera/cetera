Manage Survey Answers
================

The purpose of this functionality is to provide APIs to create, retrieve user answers to questionnaire.

Business Requirements
---------------------
* Advisors may take surveys multiple times. Backend will save the history of survey answers, and generate iQuantify value, and resources links.

Detailed Process Flow
---------------------
1. User navigate to survey page.
2. Front-end calls API to get latest Questionnaire.
3. User finish all survey questions and submit.
4. Front-end calls API to save answers in DB.
5. Back-end will calculate iQuantify and performance value and resource links and save to DB.
6. If user navigate to Resource page directly, Front-end will need to call API to get the latest answers and resource links for the person.

API Details
-----------

All of the following APIs need to check that they can be called only by a person with valid session.

### create new answers ###
    /api/answers
    @RequestMapping(method = RequestMethod.PUT)
        public AnswersResponse create(@RequestHeader("X-CS-Auth") String auth,
                                      @RequestHeader("X-CS-Session") String sessionId,
                                      @RequestBody AnswersRequest answersRequest)

#### Fetch details of the latest answers and resource for a person ####

    /api/answers
    @RequestMapping(method = RequestMethod.GET)
    public AnswersResponse get(@RequestHeader("X-CS-Auth") String auth,
                               @RequestHeader("X-CS-Session") String sessionId)

Request and Response Models
-----------
AnswersRequest {
    String personId;
    Long questionnaireId;
    List answer;
}

AnswersResponse {
    Integer iQuantify;
    BigDecimal performance;
    String advisorResources;
}
