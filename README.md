# qaf-support-ws
 Support project that provides ready to use steps for webservices 
 
 To start with 
 1. [Create QAF Project](https://qmetry.github.io/qaf/latest/create_test_project.html)
 2. add dependency
    ```
    <dependency org="com.qmetry" name="qaf-support-ws" rev="latest.integration" />
    
    ```
 3. Create web-service call repository having all your calls in following format in `.wsc` or `.properties` file
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
 rest.client.impl=com.qmetry.qaf.automation.rest.client.BasicAuthRestClient
 rest.client.basic.auth.username=<USERNAME>
 rest.client.basic.auth.password=<PASSWORD>
 ```
2. For Digest type authentication, please set below properties.
 
 ```
 rest.client.impl=com.qmetry.qaf.automation.rest.client.DigestAuthRestClient
 rest.client.digest.auth.username=<USERNAME>
 rest.client.digest.auth.password=<PASSWORD>
 ```
