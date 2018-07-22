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
       <id>job-generation</id>
       <snapshots>
           <enabled>true</enabled>
       </snapshots>
       <releases>
           <enabled>true</enabled>
       </releases>
       <url>https://packagecloud.io/vamsi1995/azkaban-job-generation-plugin/maven2</url>
   </pluginRepository>
 </pluginRepositories>
       
       
```

```      
            <plugin>
                <groupId>azkaban-job-generation</groupId>
                <artifactId>job-generation-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <phase>install</phase>
                        <goals>
                            <goal>job_generation</goal>
                        </goals>
                    </execution>

                </executions>
            </plugin>
```

3. Parameters of plugin and default values

```parameter```       =>         ```default_value```

```jobsFile```        =>         project.basedir/src/main/resources/flows.xml

```outputDirectory``` =>         project.build.directory

```zipFile```         =>          azkaban

4. After successful build of project, job files and azkaban.zip will be created in target folder.


### LICENSE

[MIT](https://github.com/vamsi1995/azkaban-job-generation-plugin/blob/master/LICENSE)