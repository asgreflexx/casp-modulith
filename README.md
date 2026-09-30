# AdminV2

## Changes

### card-rest-controller

* **POST** /card
    * The cards are saved when the member is saved. **POST** /member
* **GET** /card/{id}
    * When the member is retrieved, it comes with its cards. **GET** /member/{id}
* **DELETE** /card/{id}
    * Delete the cards in the member and save the member. **POST** /member
* **GET** /card/by-member-id/{memberId}
    * When the member is retrieved, it comes with its cards. **GET** /member/{id}

### MemberDto

* `casp.web.backend.presentation.layer.dtos.member.MemberDto.cardDtoSet` is obsolete.
    * Use `casp.web.backend.data.access.layer.member.Member.cards` instead
* `casp.web.backend.presentation.layer.dtos.member.MemberDto.dogHasHandlerDtoSet` is type
  `casp.web.backend.presentation.layer.dtos.member.DogHasHandlerDto`
    * `casp.web.backend.presentation.layer.dtos.member.DogHasHandlerDto` contains
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.id`
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.dog.id` is `dogId`
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.dog.name` is `dogName`

### DogDto

* `casp.web.backend.presentation.layer.dtos.dog.DogDto.dogHasHandlerDtoSet` is type
  `casp.web.backend.deprecated.dog.dtos.DogHasHandlerDto`
    * `casp.web.backend.presentation.layer.dtos.member.DogHasHandlerDto` contains
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.id`
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.member.id` is `memberId`
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.member.firstName` is `firstName`
        * `casp.web.backend.data.access.layer.dog.DogHasHandler.member.firstName` is `firstName`

### dog-has-handler-rest-controller

These URLs are obsolete:

* **GET** /dog-has-handler/by-dog-id/{dogId}. Use instead **GET** /dog/{id}
* **GET** /dog-has-handler/by-member-id/{memberId}. Use instead **GET** /member/{id}
* **GET** /dog-has-handler/members-by-dog-id/{dogId}. Use instead **GET** /dog/{id}
* **GET** /dog-has-handler/dogs-by-member-id/{memberId}. Use instead **GET** /member/{id}
* **DELETE** /dog-has-handler/by-dog-id/{dogId}. Use instead **DELETE** /dog/{id}
* **DELETE** /dog-has-handler/by-member-id/{memberId}. Use instead **DELETE** /member/{id}
* **GET** /dog-has-handler/dog-has-handler-ids-by-dog-id/{dogId}. Use instead **GET** /dog/{id}
* **GET** /dog-has-handler/dog-has-handler-ids-by-member-id/{memberId}. Use instead **GET** /member/{id}

## Findings

### DBRef

In a Spring Data MongoDB context, the @DBRef annotation denotes a reference to another document stored in a different
collection. When you change the field decorated with @DBRef in your application and subsequently save the parent
document, the changes to the referenced document (field) are not automatically persisted to the MongoDB database. This
means that altering the reference itself does not inherently propagate changes to the actual referenced document.

### @Transactional

* Cannot be used, it throws the following error:
  `Caused by: com.mongodb.MongoQueryException: Command failed with error 20 (IllegalOperation): 'Transaction numbers are only allowed on a replica set member or mongos' on server localhost:34271`
    *
  See [Mongodb v4.0 Transaction, MongoError: Transaction numbers are only allowed on a replica set member or mongos](https://stackoverflow.com/a/51462024/1066054)
    * This example is interesting:
      `transactionTemplate.executeWithoutResult(ignore -> dogHasHandlers.forEach(this::setDogAndMemberIfTheyAreNull));`
        * See `casp.web.backend.dog.DogHasHandlerServiceImpl.getDogHasHandlersByIds`
        * See how to configure a TransactionTemplate on commit: `#1 Remove unneeded MongoClient`

### How to Unit Test an Abstract Class

See [How to Unit Test an Abstract Class](https://enterprisecraftsmanship.com/posts/how-to-unit-test-an-abstract-class/)

    ...  test class per concrete production class, where you create a test class per each concrete class of the hierarchy.

### Exception <-> HttpStatus Map

| **Java Exception**                        | **HTTP Status**              | **Explanation**                                                                                                                               |
|-------------------------------------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `IllegalArgumentException`                | `400 Bad Request`            | The client sent an invalid request (e.g., incorrect parameters).                                                                              |
| `IllegalStateException`                   | `400 Bad Request`            | The client sent an invalid request (However, this would only happen if the exception is due to user input or action, not pure server logic.). |
| `MethodArgumentNotValidException`         | `400 Bad Request`            | Used in Spring for invalid request body (e.g., validation errors).                                                                            |
| `MethodArgumentTypeMismatchException`     | `400 Bad Request`            | The client submitted a request that fails validation or has incompatible parameter types                                                      |
| `MissingServletRequestParameterException` | `400 Bad Request`            | A required request parameter is missing.                                                                                                      |
| `HttpRequestMethodNotSupportedException`  | `405 Method Not Allowed`     | The HTTP method used is not supported by the endpoint.                                                                                        |
| `HttpMediaTypeNotSupportedException`      | `415 Unsupported Media Type` | The media type of the request is not supported.                                                                                               |
| `ResourceNotFoundException` (custom)      | `404 Not Found`              | Resource requested by the user does not exist.                                                                                                |
| `NoSuchElementException`                  | `404 Not Found`              | Typically used to indicate a missing resource (not always HTTP-based).                                                                        |
| `UnauthorizedException` (custom)          | `401 Unauthorized`           | The client must authenticate itself before accessing the resource.                                                                            |
| `AccessDeniedException`                   | `403 Forbidden`              | The user is authenticated but does not have the necessary permissions.                                                                        |
| `ConflictException` (custom)              | `409 Conflict`               | A conflict occurred (e.g., duplicate resource creation).                                                                                      |
| `DuplicateKeyException`                   | `409 Conflict`               | A duplicate entry issue, (`email`, `username`, etc.) that already exists in the database                                                      |
| `UnsupportedOperationException`           | `405 Method Not Allowed`     | Operation not supported by the system or endpoint.                                                                                            |
| `InternalServerErrorException` (custom)   | `500 Internal Server Error`  | Generic internal server error.                                                                                                                |
| `ConstraintViolationException`            | `400 Bad Request`            | Violation of a database or validation constraint.                                                                                             |
| `TimeoutException`                        | `504 Gateway Timeout`        | The server took too long to fulfill the request.                                                                                              |
| `CustomRateLimitException`                | `429 Too Many Requests`      | Too many requests were made in a given time period.                                                                                           |
