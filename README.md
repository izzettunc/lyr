Lyr is a CLI tool that analyzes cloud health and security across live environments and IaC artifacts, empowering developers with actionable insights delivered through both automation‑ready JSON reports and polished, human‑readable PDF summaries.

### Table of Contents

* [Table of Contents](#table-of-contents)
* [Preview](#preview)
* [Usage](#usage)
  * [Execution](#execution)
  * [Authentication](#authentication)
* [Installation](#installation)
  * [Prerequisites](#prerequisites)
  * [Steps](#steps)
* [Features](#features)
    * [Scan Rules](#scan-rules)
    * [Validation Rules](#validation-rules)
  * [Default Rule Set Configuration](#default-rule-set-configuration)
  * [Sample Custom Rule Set Configuration](#sample-custom-rule-set-configuration)

### Preview

![preview](doc/img/lyr-preview.png)

### Usage

#### Execution

To simply run it with default rule set

```bash
java -jar .\lyr-SNAPSHOT-0.0.1.jar scan-env aws
```

Also you can provide your own custom rule set configuration

```bash
java -jar .\lyr-SNAPSHOT-0.0.1.jar scan-env aws -c "path/to/rule/set/config/file.yaml"
```

See [sample custom rule set configuration](#sample-custom-rule-set-configuration) for details

#### Authentication

> [!NOTE]
> While it's on the roadmap to improve authentication methods, at this time only authentication methods are the default aws profile or default AWS access key environment variables.

```bash
aws login # Follow the login flow of aws cli
```

### Installation

> [!NOTE]
> While it's on the roadmap to create native images or at the least already build jar as a release at this time to install you have to manually build it.

#### Prerequisites

- Java 25
- Maven
- AWS CLI

#### Steps

As per version 0.0.1, you can install lyr by following steps.

```bash
git clone https://github.com/izzettunc/lyr.git
cd lyr
mvn clean install # You will find your jar in newly created target folder
```

### Features

- Human‑readable console report
- Customizable rule sets allowing you to choose which checks to run

##### Scan Rules
- Detect idle DynamoDB tables
- Identify active Glue sessions with long idle timeouts
- Flag Lambda functions with unbounded concurrency

##### Validation Rules
- Validate that a Lambda function exists
- Validate Lambda concurrency configuration
- Validate Lambda trigger state
- Validate that an SSM parameter exists
- Validate SSM parameter value

#### Default Rule Set Configuration
```yaml
scan.dynamodb.table.idle:
  maxIdlePeriodInDays: 30
  excludeEmptyTables: true
  
scan.glue.session.activeWithLongIdleTimeout:
  maxIdleTimeoutInMinutes: 15
  
scan.lambda.function.withUnboundedConcurrency:
```
#### Sample Custom Rule Set Configuration
```yaml
# Syntax:
# - scan.<service>.<resource>.<check>: <parameters>
# - validate.<service>.<resource>.<check>: <parameters>

scan.dynamodb.table.idle:
  maxIdlePeriodInDays: 180
  excludeEmptyTables: true

scan.glue.session.activeWithLongIdleTimeout:
  maxIdleTimeoutInMinutes: 15

scan.lambda.function.withUnboundedConcurrency:

validate.lambda.function.exists:
  - my-lambda-function-name
  - my-other-lambda-function-name

validate.lambda.function.concurrency:
  my-lambda-function-name: 5
  my-different-lambda-function-name: 1

validate.lambda.function.trigger.state:
  my-lambda-function-that-should-be-enabled-all-the-time-name: Enabled
  my-lambda-function-that-should-be-disabled-all-the-time-name: Disabled

validate.ssm.parameter.exists:
  - my-smm-parameter

validate.ssm.parameter.value:
  my-smm-parameter: "valueThisParameterSupposedToHave"
```