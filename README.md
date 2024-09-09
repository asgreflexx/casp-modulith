# Findings
## @Transactional
* Cannot be used, it throws the following error: 
  `Caused by: com.mongodb.MongoQueryException: Command failed with error 20 (IllegalOperation): 'Transaction numbers are only allowed on a replica set member or mongos' on server localhost:34271`
  * See [Mongodb v4.0 Transaction, MongoError: Transaction numbers are only allowed on a replica set member or mongos](https://stackoverflow.com/a/51462024/1066054)
  * This example is interesting:
    `transactionTemplate.executeWithoutResult(ignore -> dogHasHandlers.forEach(this::setDogAndMemberIfTheyAreNull));`
    * See `casp.web.backend.business.logic.layer.dog.DogHasHandlerServiceImpl.getDogHasHandlersByIds`
