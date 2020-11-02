# 2018_Assignments

In this repository we will put the assignments and helpful additional files that you need for the assignment.


## Submitting assignments

Every group is given a repository, but no access to the master branch. Instead, you work on the development branch, and make pull requests to submit your assignments. When you do, your program will be sent to a continuous integration provider: [CirleCI](https://circleci.com/). CircleCI will run your tests, and only report success if all tests succeed. CircleCI is configured to build the currently assigned project by running `mvn integration-test`. If your project does not compile and pass all JUnit tests, then it will automatically be prevented from merging into the master branch. Any project which does not pass CircleCi will not be graded!

 **Don't touch .circleci/config.yml**.

To get started, clone the repository you've been given. (on the page of your own repository there should be a _clone or download_-button, which will provide you with the exact url). To use SSH, follow [Connecting to GitHub with SSH](https://help.github.com/articles/connecting-to-github-with-ssh/). Check out the development branch (`git checkout development`).

In the new repo, do `git remote add assignments https://github.com/rug-advoop/2018_Assignments.git` (only once) to allow downloading assignments, then `git pull assignments master` to get the latest version of the assignments. When the repository updates, repeat the latter (expect a Nestor announcement). If git refuses, `git pull --allow-unrelated-histories assignments master` might work. Make an issue in the repository otherwise.

## Code Coverage

For this year, we're providing POM files that compute code coverage: The degree to which your code is tested. This is done by the [JaCoCo Maven Plugin](http://www.jacoco.org/). The plugin will generate reports on build, and place them in ${PROJECT_ROOT}/target/site/jacoco. A human readable format is produced by opening index.html in that folder with a web browser. NOTE: JaCoCo is configured to work on `mvn test` or later targets.

In the CI config there is a bash line that checks if the coverage is at least 100%. Use the following ```if test ` cat target/site/jacoco/index.html | grep -o "[[:digit:]]\{1,3\}%" | head -1 | grep -o "[[:digit:]]\{1,3\}" ` -ge 100 ; then echo "exit code 0 (pass)" ; else echo "exit code 1 (fail)" ; fi ;``` to check if your code will pass that particular test.
