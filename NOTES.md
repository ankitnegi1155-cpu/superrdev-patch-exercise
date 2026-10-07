  # Summary of changes
#### •	TaskRepository.java (lines 14–18): The search query combined AND/OR conditions without proper grouping, causing the AND clause to evaluate before OR and breaking the "search by term + optional status filter" logic. Fixed by wrapping each condition pair in parentheses so the term search and optional status filter evaluate as intended, e.g. (archived = FALSE AND LOWER(title) LIKE :term) OR (LOWER(description) LIKE :term AND (:status IS NULL OR status = :status)).

> #### •	TaskController.java (lines 50–55): Pagination parameters (page, pageSize) had no validation, allowing 0 or negative values to pass through. Added a guard clause returning HttpStatus.BAD_REQUEST when either value is invalid, before executing the query.

#### •	TaskController.java (lines 32–34): Status filtering only checked for null/empty values but didn't validate against the three valid TaskStatus enum values (OPEN, IN_PROGRESS, DONE). Added exception handling for IllegalArgumentException (invalid status string) and NullPointerException (null status), each returning a clear error message.
    
  ## What I chose not to change (and why)
#### •	Did not refactor controller-level validation to a global `@ControllerAdvice`/Bean Validation setup: improves consistency, but it’s a broader structural change than needed for the targeted bug fixes.
> #### •	Did not add “Lombok” dependency, it can help in reducing the code by writing Getters and Setters in Task class automatically just by adding @Setter, @Getter annotation.
  ## Biggest remaining risk
#### •	We could have used “Pageable” class provided by JPA to search.
> #### •	There are no Unit Tests, Integration Tests to check edge cases. 
  ## Tools/AI used
#### •	Used “Claude and ChatGPT” to spot the `AND/OR` precedence issue in the query and to draft corrected parenthesized logic; then adjusted accordingly.
