## azkaban-job-generation-plugin

Generates Azkaban jobs in zip format by taking flows in xml file.

### Features

* Representing flows in simple xml format

* During build it will validate every flow whether it is a valid DAG or not.

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

2. Create a new module in your project which is clone of job-generation-plugin module of the repo.

3. Add following in the module pom where flows.xml file is present.

```       <plugin>
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

Default location of flows.xml file resources folder of module 

we can also set as a ```flowsFile```parameter

4. After successful build of project, job files and azkaban.zip will be created in target folder.


### LICENSE

[MIT](https://github.com/vamsi1995/azkaban-job-generation-plugin/blob/master/LICENSE)