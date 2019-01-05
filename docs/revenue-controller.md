Manage Revenue Calculations
================

The purpose of this functionality is to provide APIs to calculate iQuotient and Risk value.

Business Requirements
---------------------
Users provide Revenue, Qualified-Perc and Non-Recurring-Perc, and get the iuQuotient and Risk value.

API Details
-----------

### Calculate iQuotient and Risk value ###
    @RequestMapping(method = RequestMethod.GET)
    public IQuotientResponse calculate(@RequestHeader("X-CS-Auth") String auth,
                         @RequestParam(value = "yearlyRevenue") BigDecimal yearlyRevenue,
                         @RequestParam(value = "qualifiedPerc") BigDecimal qualifiedPerc,
                         @RequestParam(value = "nonRecurringPerc") BigDecimal nonRecurringPerc)

Request and Response Models
-----------

IQuotientResponse {
    BigDecimal iQuotientValue;
    BigDecimal iQuotientRisk;
    BigDecimal nonQualifiedPerc;
    BigDecimal recurringPerc;
}