# Fujitsu Internship Application Project 2024
 
What I did:
- Database
- REST endpoints
  - get delivery fee (with optional timestamp)
  - change regional fee
  - change extra fee
- Initial values stored and read in from .xml resource
  - I figured it is convenient to use XML, because some needs to be parsed in the project already
- CronJob
- Custom exceptions and exception handling
- API tests

The endpoints:
- http://localhost:8080/fee
  - GET request, for getting a delivery fee
  - parameters:
    - city
      - tallinn
      - tartu
      - parnu
    - vehicle
      - car
      - scooter
      - bike
    - timestamp
      - epoch time
- http://localhost:8080/fee/regional
  - POST request, for setting one of the regional fees
  - parameters:
    - cost
      - integer value in eurocents (100 is parsed as 1€)
    - city
    - vehicle
- http://localhost:8080/fee/extra
  - POST request, for setting one of the extra fees
  - parameters:
    - cost
    - category
      - string value from ExtraFeeCategory enum
    - vehicle

Some of the tests fail due to my inexperience
with testing in Java (I don't usually use this
language), but I tried very hard and did my best!
I go into more detail in the FeeControllerTests file.