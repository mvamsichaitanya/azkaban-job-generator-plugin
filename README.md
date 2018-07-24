## azkaban-job-generation-plugin

Generates Azkaban jobs in zip format by taking flows in xml file.

### Features

* Representing flows in simple xml format

* During build it will validate every flow whether it is a valid DAG or not and generates zip of job files in target directory.

### How to use.

1. Create xml file with flows and their jobs as shown in below sample file

```
<flows>

    <flow name="flow1">
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

2. Add following plugin repository and plugin to project pom.xml.

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
                <version>1.0.0</version>
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

3. Parameters of plugin and default values

    ```parameter```       =>         ```default_value```
    
    ```resourcesPath```   =>         ${project.basedir}/src/main/resources/
    
    ```jobsFile```        =>         flows.xml
    
    ```outputDirectory``` =>         ${project.build.directory}
    
    ```zipFile```         =>          azkaban

4. After successful build of project, job files and azkaban.zip will be created in target folder.

5. azkaban.zip contains job files generated from flows.xml and properties file present in ```resourcesPath```

6. following parameters can be set for job.

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