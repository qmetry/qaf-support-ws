[![License](https://img.shields.io/github/license/qmetry/qaf-support-ws.svg)](http://www.opensource.org/licenses/mit-license.php)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qmetry/qaf-support-ws/badge.svg)](https://mvnrepository.com/artifact/com.qmetry/qaf-support-ws/latest)
[![GitHub tag](https://img.shields.io/github/tag/qmetry/qaf-support-ws.svg)](https://github.com/qmetry/qaf-support-ws/tags)
[![javadoc](https://javadoc.io/badge2/com.qmetry/qaf-support-ws/javadoc.svg)](https://javadoc.io/doc/com.qmetry/qaf-support-ws)

# qaf-support-ws
 Support project that provides ready to use steps for webservices 
 Features:

 | Feature	|Attribute	|Details|
 |---------|----------|--------|
 |Test-Authoring	|	Industry Standard||
 |	|Coded	|Java (TestNG, Junit)|
 ||		BDD |	QAF-BDD, Gherkin||	
 ||		Keyword driven	|XML,CSV,EXCEL||
 | Step-Creation	|||
 ||Java	|Y (any Java IDE can be used)|
 ||BDD	| Ecipse QAF BDD editor, QAS|
 ||Keyword driven|	QAS||
 |IDE support	|	Y | Eclipse, IntelliJ, Net-beans, QAS||
 |Data-driven		|Y |XML, CSV, JSON, DB, Excel|
 |Reporting		|XML	|Industry Standard – TestNG and Junit|
 ||HTML |TestNG, Junit, QAF(json) detailed reporting|
 |Language-Support|Y|Java, Goovy|
 |Programming|Y		Full programming control|
 |Test-Data|||
 ||parameter|Y|
 ||Random|	Y|
 ||Locale	|Y|
 ||	Multiple Env support|	Y|
 |Orchestration|Y|		UI and API test Orchestration|
 |Test-Management|Y|		Supports easy integration with all test management tools|
 |Build tools	|Industry Standard|	ANT+IVY, Maven,Gradle|
 |Request validation|||
 ||XML	|xml path validation|
 ||JSON	|json path validation|
 ||Schema|	Y|
 |Run-configuration|Y|		TestNG XML (all features)|
 |||Parallel execution|
 |SOAP support	|Y||
 |REST support		|Y||

 To start with 
 1. [Create QAF Project](https://qmetry.github.io/qaf/latest/create_test_project.html)
 2. add dependency
    ```
    <dependency org="com.qmetry" name="qaf-support-ws" rev="latest.integration" />
    
    ```
 3. Create [web-service call repository](https://github.com/qmetry/qaf-support-ws/wiki/Request-Call-Repository) having all your calls in following format in `.wsc` or `.properties` file
    ``` properties
    #format
    #my.ws.call = {'headers':<json map of key-val pair>,'endpoint':'/myservice-endPoint','baseUrl':'${env.baseurl}','method':'<method GET|POST|PUT|DELETE>','query-parameters':<json map of key-val pair>,'form-parameters':<json map of key-val pair>,'body':'<request-body>'}
    get.sample.call={'headers':{},'endPoint':'/myservice-endpoint','baseUrl':'${env.baseurl}','method':'GET','query-parameters':{'param1':'val1','param2':'val2'},'form-parameters':{},'body':''}
    
    
    ```
  4. Create bdd
   ```
   SCENARIO: <scenario name>
   META-DATA: {"description":"<scenario description>",<other metadata>}

      When user requests '${get.sample.call}'
      Then response should have status code '<status code>'
     [And response should have '<expectedvalue1>' at '<jsonpath1>'
      And response should have '<expectedvalue2>' at '<jsonpath2>'
        :
        :
     ]  
   END
   ```
   
 5. [Run your test](https://github.com/qmetry/qaf-step-by-step-tutorial/wiki/Exercise-3-Run-Your-First-Test)
 
 Here you can find [Example Projet](https://github.com/qmetry/WebServiceWithWebAutomation-Sample)
 
# Authorization

 This dependency provides few well known Authentication support as well.
 
1. For Basic authentication, please set below properties.
 
 ```
 rest.client.impl=com.qmetry.qaf.automation.ws.client.BasicAuthWsClient
 rest.client.basic.auth.username=<USERNAME>
 rest.client.basic.auth.password=<PASSWORD>
 ```
2. For Digest type authentication, please set below properties.
 
 ```
 rest.client.impl=com.qmetry.qaf.automation.ws.client.DigestAuthWsClient
 rest.client.digest.auth.username=<USERNAME>
 rest.client.digest.auth.password=<PASSWORD>
 ```
