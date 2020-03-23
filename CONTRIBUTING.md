# Contributing Guidelines

## Branches

All branches must be named as follows:
* `master`
* `experimental`
* `issue/ISSUE-ID-ISSUE-TITLE`
* `feature/ISSUE-ID-ISSUE-TITLE`

### Master Branch
There is nothing to commit directly to the `master` branch. Only the experimental branch may be merged into the master branch. Exceptions are *hotfixes*.

#### Hotfixes
Hotfixes are reserved for errors which are so serious that they must be corrected quickly and specifically. Hotfixes must have `hotfix: TITLE` as their commit message.

### Experimental
Only branches with the name `issue/ISSUE-ID-ISSUE-TITLE`, `feature/ISSUE-ID-ISSUE-TITLE` or user branches may be merged into the `experimental` branch. Heads are also allowed to pushed to `experimental` when fixing e.g. spelling mistakes. This should be done as rarely as possible.

## Commit Messages
Commit messages must correspond to the following template: `MESSAGE (#ISSUE-ID)`.
- Commit messages must have a short and **meaningful** description as their title, so something simple like "*bug fix*" is unacceptable. Also, special characters like `;:<>-+[]()` etc. are unacceptable
- Commit messages must be written in english and past tense
- More detailed descriptions are acceptable, as long as they are not in the first line of the commit message
- If the commit is made without an issue, the issue id may be omitted.
- Exceptions are hotfixes. More about this in *hotfixes*.

For merges, the commit message must be `merge from SOURCE to DESTINATION (#PULLREQUEST-ID)`

## Pull Requests

### Title
Title of the associated issue *or* a short description if no issue exists.

### Content
In the pull request, all fields of the template must be filled in meaningfully. If this is not the case, the pull request is closed and tagged with the label `invalid`.  It can be reopened at any time after it has been corrected.

## Issues

### Title
The title of an issue should be a *very* concise summary of the content and must not contain any special characters such as `'[]()\/;+-*`. The only exception to this is `:`.

### Contents
An issue must match one of the given issue templates (currently *FEATURE_REQUEST.md* and *BUG_REPORT.md*). Fields that do not add new or meaningful information to the issue may be removed. If not, the issue will be closed and tagged with the label `invalid`. It can be reopened at any time after it has been corrected. 

# Code Style

In general, it can be said that programming is done in english, meaning that all variable names and comments are to be written in English.

# Other
- If there is a circumstance which is not clearly defined here, it has to be discussed with the correct backend head and will be inserted here if necessary.
- Suggestions for improvement are welcome

## Versioning
We use the [Semantic Versioning 2.0.0](https://semver.org/)

Current Version: 0.3.0
