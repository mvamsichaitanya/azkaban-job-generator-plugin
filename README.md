## azkaban-job-generation-plugin

azkaban-job-generation-plugin generates Azkaban flows in zip format by taking flows in xml file.
It also validates DAG of the flow during build.

Available from the [Central Repository](https://search.maven.org/#artifactdetails%7Cio.github.mvamsichaitanya%7Cazkaban-job-generation%7C1.0.0%7Cmaven-plugin) 

### Features

* Representing flows and property files in simple xml format

* During build it will validate every graph of the flow with following algorithms
 
   => Cycle detection (throws Exception if cycle is detected)
 
   => Graph connectivity (throws Exception if jobs of the flow are not connected)
 
* Generates zip of job files and property files in output directory with following structure.
  
   => A separate folder will be created for each flow.
   
   => flow specific property files are kept in their respective folder
   
   => global or shared property files are kept in root folder 


### How to use.

1. Create xml file with flows and their jobs,property files as shown in below sample file

```
<flows>

    <propertyFiles>
        <file>shared.properties</file>
    </propertyFiles>

    <flow name="flow1">
       
        <propertyFiles>
            <file>flow1.properties</file>
        </propertyFiles>

        <job name="job1">
            <command>.....</command>
            <arguments>....</arguments>
        </job>
        
        <job name="job2">
            <command>....</command>
            <arguments>.....</arguments>
            <dependency>job1</dependency>
        </job>
        
        <job name="job3">
            <command>....</command>
            <arguments>.....</arguments>
            <dependency>job2</dependency>
        </job>
    
    </flow>
    
    <flow>
    .....
    .....
    .....
    </flow>

</flows>
```

2. Global property files are mentioned in the parent node and flow specific property files are present in flow node.

3. Add following plugin repository and plugin to project pom.xml.

```    
 <pluginRepositories>
        <pluginRepository>
            <id>oss-sonatype</id>
            <name>oss-sonatype</name>
            <url>https://oss.sonatype.org/content/repositories/releases/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
 </pluginRepositories>
       
       
```

```      
            <plugin>
                <groupId>io.github.mvamsichaitanya</groupId>
                <artifactId>azkaban-job-generation</artifactId>
                <version>1.0.1</version>
                <inherited>false</inherited>
                <configuration>
                    <resourcesPath>${project.basedir}/src/main/resources/</resourcesPath>
                    <jobsFile>flows.xml</jobsFile>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>job_generation</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```


4. Parameters of plugin and their description

    ```parameter```       =>         ```description```
        
    ```jobsFile```        =>         Name of xml file where flows are mentioned in above specified format
    
    ```resourcesPath```   =>         Path where jobsFile and property files are present

    ```outputDirectory``` =>         Output directory where zip file to be generated
    
    ```zipFile```         =>          Name of the zip file
    

5. Parameters of plugin and default values

    ```parameter```       =>         ```default_value```
    
    ```resourcesPath```   =>         ${project.basedir}/src/main/resources/
    
    ```jobsFile```        =>         flows.xml
    
    ```outputDirectory``` =>         ${project.build.directory}
    
    ```zipFile```         =>          azkaban



6. After successful build of project and azkaban.zip and unzip of azkaban.zip will be created in output directory.

7. Structure of zip file will be as following:-

   => A separate folder will be created for each flow.
   
   => flow specific property files are kept in their respective folder
   
   => global or shared property files are kept in root folder 
   
8. azkaban.zip contains job files generated from flows.xml and properties file present in ```resourcesPath```

9. following parameters can be set for job.

```
      command
      arguments
      working.dir
      retries
      retry.backoff
      failure.emails
      success.emails
      notify.emails
      dependency

```

### LICENSE

[MIT](https://github.com/mvamsichaitanya/azkaban-job-generator-plugin/blob/master/LICENSE.txt)