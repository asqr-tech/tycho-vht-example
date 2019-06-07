# Eclipse Maven Tycho Build

## Maven Project for Tycho Configuration

Create an Eclipse Maven Configuration Project (**Name for the Maven project: "com.vht.openvxml.configuration"**) with pom style packaging. Here we will be congifuring the Tycho setting and the required project repositories. The repositories we use for the project must be in p2 layout. Our pom file must be set to following configuration to download all types of repositories.

```
<modelVersion>4.0.0</modelVersion>
<groupId>com.vht.openvxml</groupId>
<artifactId>com.vht.openvxml.configuration</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>pom</packaging>
<properties>
	<tycho.version>1.3.0</tycho.version>
	<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<eclipse-repo.url>https://download.eclipse.org/releases/2019-03/</eclipse-repo.url>
</properties>
<repositories>
	<repository>
		<id>eclipse-2019-03-release</id>
		<url>${eclipse-repo.url}</url>
		<layout>p2</layout>
	</repository>
	<repository>
		<id>orbit-2019-03</id>
		<url><!-- compatible orbit releases for eclipse version can be found at 									https://download.eclipse.org/tools/orbit/downloads/--></url>
		<layout>p2</layout>
	</repository>
	<repository>
		<id>buildVHT-elements</id>
		<url><!-- our release elements repository url --></url>
		<layout>p2</layout>
	</repository>
	<repository>
		<id>nebula-latest-release</id>
		<url>https://download.eclipse.org/nebula/releases/latest/</url>
		<layout>p2</layout>
	</repository>
</repositories>
<build>
	<plugins>
		<plugin>
			<groupId>org.eclipse.tycho</groupId>
			<artifactId>tycho-maven-plugin</artifactId>
			<version>${tycho.version}</version>
			<extensions>true</extensions>
		</plugin>
		<!--Enable the replacement of the SNAPSHOT version in the final product 
			configuration -->
		<plugin>
			<groupId>org.eclipse.tycho</groupId>
			<artifactId>tycho-packaging-plugin</artifactId>
			<version>${tycho.version}</version>
			<executions>
				<execution>
					<phase>package</phase>
					<id>package-feature</id>
					<configuration>
						<finalName>${project.artifactId}_${unqualifiedVersion}.${buildQualifier}</finalName>
					</configuration>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<groupId>org.eclipse.tycho</groupId>
			<artifactId>target-platform-configuration</artifactId>
			<version>${tycho.version}</version>
			<configuration>
				<environments>
					<environment>
						<os>linux</os>
						<ws>gtk</ws>
						<arch>x86_64</arch>
					</environment>
					<environment>
						<os>win32</os>
						<ws>win32</ws>
						<arch>x86_64</arch>
					</environment>
					<environment>
						<os>macosx</os>
						<ws>cocoa</ws>
						<arch>x86_64</arch>
					</environment>
				</environments>
			</configuration>
		</plugin>
	</plugins>
</build>
```

## Root Maven Project Creation and Setup

**Steps to setup the Root Project**
1. Create an Eclipse Maven Project (with pom based packaging) as a child project to the above created configuration project.
2. Create an Eclipse Maven Module with the name **releng** to the Root Maven project
3. Copy the Tycho configuration project created into releng project through file explorer
4. Now to enable m2e building of project, pom file for the **Root Project** must follow below configuration.

```
<modelVersion>4.0.0</modelVersion>
<artifactId>com.vht.openvxml.root</artifactId>
<packaging>pom</packaging>
<parent>
	<groupId>com.vht.openvxml</groupId>
	<artifactId>com.vht.openvxml.configuration</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<relativePath>./releng/com.vht.openvxml.configuration</relativePath>
</parent>
<modules>
	<module>releng</module>
	<!-- modules of the project will be mentioned here -->
</modules>
<build>
	<pluginManagement>
		<plugins>
			<!--This plugin's configuration is used to store Eclipse m2e settings 
				only. It has no influence on the Maven build itself. -->
			<plugin>
				<groupId>org.eclipse.m2e</groupId>
				<artifactId>lifecycle-mapping</artifactId>
				<version>1.0.0</version>
				<configuration>
					<lifecycleMappingMetadata>
						<pluginExecutions>
							<pluginExecution>
								<pluginExecutionFilter>
									<groupId>
										org.eclipse.tycho
									</groupId>
									<artifactId>
										tycho-packaging-plugin
									</artifactId>
									<versionRange>
										[1.3.0,)
									</versionRange>
									<goals>
										<goal>
											build-qualifier-aggregator
										</goal>
									</goals>
								</pluginExecutionFilter>
								<action>
									<ignore></ignore>
								</action>
							</pluginExecution>
						</pluginExecutions>
					</lifecycleMappingMetadata>
				</configuration>
			</plugin>
		</plugins>
	</pluginManagement>
</build>
```
#### _Project Structure_

Once root Project setup is cleared, then your project structure must be in the below file structure.

```
com.vht.openvxml.root
	| --  releng
		| -- com.vht.tycho.configuration
			| -- pom.xml
	| -- pom.xml
| -- pom.xml
```

## Enable pomless Tycho build:

### .mvn/extensions.xml:

extensions.xml file helps to build projects without pom files (we require this write-up to work with feature projects designed for the plugins). The content of the .mvn/extensions.xml must look like the following to enable pomless builds.

```
<extensions>
  <extension>
    <groupId>org.eclipse.tycho.extras</groupId>
    <artifactId>tycho-pomless</artifactId>
    <version>1.3.0</version>
  </extension>
</extensions>
```

#### _Project Structure_

Once enabling of POMless build is cleared, then your project structure must be in the below file structure.

```
com.vht.openvxml.root
	| -- .mvn
		| -- extensions.xml
	| --  releng
		| -- com.vht.tycho.configuration
			| -- pom.xml
	| -- pom.xml
| -- pom.xml
```

### updatesite project

Here we will be collecting the compiled plugins for the project. Follow below steps to generate such plugins

1. Generate maven module (Name: com.vht.openvxml.update) with packaging set to "eclipse-repository" as a child to releng project(which 	    is in turn a child to root project)
2. Generate a category.xml project in the update module

#### _POM file Structure to all Maven Modules_

Below is the generic structure that we follow for all the Maven modules we create for the Tycho Project

```
<modelVersion>4.0.0</modelVersion>
<artifactId>com.vht.openvxml.releng<!-- artifact id of the module you are creating --></artifactId>
<packaging>pom<!-- packaing format --></packaging>

<parent>
	<groupId>com.vht.openvxml</groupId> <!-- always stays the same for all the internal projects -->
	<artifactId>com.vht.openvxml.root</artifactId> <!-- parent maven project/module artifact id -->
	<version>1.0.0-SNAPSHOT</version>
</parent>

<modules>
	<module>com.vht.openvxml.update</module> <!-- child modules we add the maven project/module will be added here -->
</modules>
```

#### _Project Structure_

This step finalizes the basic setup structure of Maven Tycho Build. At the moment the project structure must look like the below mentioned structure.

```
com.vht.openvxml.root
	| -- .mvn
		| -- extensions.xml
	| --  releng
		| -- com.vht.tycho.configuration
			| -- pom.xml
		| -- com.vht.openvxml.update
			| -- category.xml
			| -- pom.xml
	| -- pom.xml
| -- pom.xml
```


## Validating the structure

Now run the build via mvn clean verify, this build should be successful by generating a target folder with basic p2 repository layout within the com.vht.openvxml.update project. The modified project structure will be as below

#### _Project Structure_

```
com.vht.openvxml.root
	| -- .mvn
		| -- extensions.xml
	| --  releng
		| -- com.vht.tycho.configuration
			| -- pom.xml
		| -- com.vht.openvxml.update
			| -- target
				| -- p2agent
					| -- org.eclipse.equinox.p2.core
					| -- org.eclipse.equinox.p2.engine
				| -- repository
					| -- features
					| -- plugins
					| -- artifacts.jar
					| -- artifacts.jar.xz
					| -- content.jar
					| -- content.jar.xz
					| -- p2.index
				| -- targetPlatformRepository
					| -- content.xml
				| -- category.xml
				| -- com.vht.openvxml.update-1.0.0-SNAPSHOT.zip
				| -- local-artifacts.properties
				| -- p2artifacts.xml
				| -- p2content.xml
			| -- category.xml
			| -- pom.xml
	| -- pom.xml
| -- pom.xml
```

## Building plug-ins for the Tycho Implementation

It is a general process of creating eclipse plugin projects through eclipse

## Building features for the Tycho Implementation

It is also a general process feature project creation through eclipse and remember to add required dependency plugins and/or features to generated feature project


## Adding Plugins and Features to the Maven Tycho Project

Once the Plugins and Featrues generation is completed follow below steps

**Steps to setup the Plugins and Features to the Maven Tycho Root Project**

1. create a eclipse maven module (with pom style packaging) as a child to root.
2. 


















Created bundles directory in main project directory and a new plug-in project **com.vht.tycho.plugin1** in bundles directory. Created a pom.xml file in **com.vht.tycho.plugin1**. 

```
main directory
	| --  releng 
		| -- com.vht.tycho.configuration
			| -- pom.xml
	| --  bundles 
		| -- com.vht.tycho.plugin1
		| -- pom.xml
	| -- pom.xml
```

### Add pom for bundles directory

Created the following pom in the bundles directory.
```
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.vht.tycho</groupId>
    <artifactId>com.vht.tycho.bundles</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>com.vht.tycho</groupId>
        <artifactId>com.vht.tycho.root</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <modules>
        <module>com.vht.tycho.plugin1</module>
    </modules>
</project>
```
Here `<parent>` tag refers to the root project directory.

### Add bundle to root pom
```
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.vht.tycho</groupId>
    <artifactId>com.vht.tycho.root</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <parent>
            <groupId>com.vht.tycho</groupId>
            <artifactId>com.vht.tycho.configuration</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        <relativePath>./releng/com.vht.tycho.configuration</relativePath>
    </parent>
    <modules>
        <module>bundles</module>
    </modules>
</project>
```

### Run the build and validate the result

To run the build on the command line from your root directory via mvn clean verify. In console you should see the BUILD SUCCESS statement.

Press F5 on your project in the Eclipse IDE to refresh it. You find a new folder called target in your project which contains the JAR file for your plug-in. The created JAR file still has the **SNAPSHOT** suffix. _This suffix is replaced with the build time stamp once you build a product or an update site with the **eclipse-repository** packaging type._

**Note**: You can also run the build from the bundles directory or the directory of the plug-in. This build should also run successfully.

## Tycho build for Eclipse features

Created a new directory called features in your main directory. And Created a new **feature project** called **com.vht.tycho.feature** in the features folder.

Added the **com.vht.tycho.plugin1** plug-in to the feature.

Above step is done using Plug-ins and Fragments Eclipse UI or by updating feature.xml in feature project like below,
```
	<plugin
         	id="com.vht.tycho.plugin1"
	 	download-size="0"
		install-size="0"
		version="0.0.0"
		unpack="false"/>
```
```
main directory
	| --  releng 
		| -- com.vht.tycho.configuration
			| -- pom.xml
	| --  bundles 
		| -- com.vht.tycho.plugin1
		| -- pom.xml
	| --  features 
		| --  com.vht.tycho.feature
		| -- pom.xml
	| -- pom.xml
```

### Create pom for feature folder

Created the following pom in the features directory.

```
<project>
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.vht.tycho</groupId>
	<artifactId>com.vht.tycho.features</artifactId>
	<version>1.0.0-SNAPSHOT</version>
    	<packaging>pom</packaging>
    	<parent>
        		<groupId>com.vht.tycho</groupId>
        		<artifactId>com.vht.tycho.root</artifactId>
        		<version>1.0.0-SNAPSHOT</version>
    	</parent>
    	<modules>
        		<module>com.vht.tycho.feature</module>
    	</modules>
</project>
```

The **com.vht.tycho.feature** feature does not require a separate pom file, as we can use the generated defaults.

### Add feature to root pom
```
<project>
	...

	<modules>
		<module>bundles</module>
		<module>features</module> 
	</modules>
</project>
```

Updated modules section in root project pom.xml with _<module>features</module>_. _<module>features</module>_ statement adds the features pom to the build.

### Run the build

Run the build from the main directory via `mvn clean verify`. This works fine and generates build.

#### Following are the log for successful build:
```
 ------------------------------------------------------------------------
[INFO] Reactor Summary for com.vht.tycho.root 1.0.0-SNAPSHOT:
[INFO] 
[INFO] com.vht.tycho.root ................................. SUCCESS [  0.517 s]
[INFO] com.vht.tycho.bundles .............................. SUCCESS [  0.026 s]
[INFO] com.vht.tycho.plugin1 .............................. SUCCESS [  4.091 s]
[INFO] com.vht.tycho.features ............................. SUCCESS [  0.033 s]
[INFO] com.vht.tycho.feature .............................. SUCCESS [  0.565 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  26.514 s
[INFO] Finished at: 2019-03-26T23:28:05+05:30
[INFO] ------------------------------------------------------------------------
```

**Notes:** 

1. Building the feature (from the features directory) will not work, as this requires com.vht.tycho.plugin1 to be present in one of the repositories specified as dependency. 
2. If an independent build of the feature is required, you need to make the plug-in available via a repository. This can for example be done by building and installing the plug-in into the local Maven repository with the mvn clean install command.
3. Press F5 on your project in the Eclipse IDE to refresh it. You find a new folder called target in your project which contains the JAR file for your plug-in. The created JAR file still has the SNAPSHOT suffix. This suffix is replaced with the build time stamp once you build a product or an update site with the eclipse-repository packaging type.

## Tycho build for update sites

The following steps explain how to build Eclipse p2 update sites with Maven Tycho.

#### Create a category definition file for the update site:

In **releng** folder, created a new project of type General named **com.vht.tycho.update**.

Right-click on this project and select File -> New -> Other… -> Plug-in Development -> Category Definition. This step creates category.xml file.

Created a new category with the "tychoexample" ID and "Tycho example" name. And included the feature project into the category.

This step updates category.xml like,
```
<site>
	<feature url="features/com.vht.tycho.feature_1.0.0.qualifier.jar" id="com.vht.tycho.feature" version="1.0.0.qualifier">
		<category name="tychoexample"/>
	</feature>
	<category-def name="tychoexample" label="Tycho example"/>
</site>
```
```
main directory
	| --  releng 
		| -- com.vht.tycho.configuration
			| -- pom.xml
		| -- com.vht.tycho.update
			| -- pom.xml
		| -- pom.xml
	| --  bundles 
		| -- com.vht.tycho.plugin1
		| -- pom.xml
	| --  features 
		| --  com.vht.tycho.feature
		| -- pom.xml
	| -- pom.xml
```

We didn't create any pom.xml for releng folder yet. In above process we are building using _<modules>_ tag in root pom.xml only.

### Create pom file for the releng folder

Create the following pom file in the releng folder.
```
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.vht.tycho</groupId>
    <artifactId>com.vht.tycho.releng</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>com.vht.tycho</groupId>
        <artifactId>com.vht.tycho.root</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modules>
        <module>com.vht.tycho.update</module>
    </modules>
</project>
```

### Create pom for the update site

Created the following pom file for your update site project.
```
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.vht.tycho</groupId>
        <artifactId>com.vht.tycho.releng</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>com.vht.tycho.update</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>eclipse-repository</packaging>
</project>
```

### Update the root pom

Need to add new module to root pom file.
```
<project>
 	<modelVersion>4.0.0</modelVersion>
 	<groupId>com.vht.tycho</groupId>
 	<artifactId>com.vht.tycho.root</artifactId>
 	<version>1.0.0-SNAPSHOT</version>
 	<packaging>pom</packaging>
 	<parent>
   		<groupId>com.vht.tycho</groupId>
   		<artifactId>com.vht.tycho.configuration</artifactId>
   		<version>1.0.0-SNAPSHOT</version>
  		<relativePath>./releng/com.vht.tycho.configuration</relativePath>
 	</parent>
	<modules>
        <module>bundles</module>
        <module>features</module>
        <module>releng</module>
    </modules>
</project>
```

`<module>releng</module>` statement adds the releng pom to the aggregator build

### Run aggregator build and validate the update site build result

Run the build from your main directory. It ran successfully and build all your components including the update site.

#### Following are the log for successful build:
```
[DEBUG] adding directory plugins/
[DEBUG] adding directory features/
[DEBUG] adding entry p2.index
[DEBUG] adding entry content.xml.xz
[DEBUG] adding entry artifacts.xml.xz
[DEBUG] adding entry plugins/com.vht.tycho.plugin1_1.0.0.201903261908.jar
[DEBUG] adding entry content.jar
[DEBUG] adding entry features/com.vht.tycho.feature_1.0.0.201903261908.jar
[DEBUG] adding entry artifacts.jar
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for com.vht.tycho.root 1.0.0-SNAPSHOT:
[INFO] 
[INFO] com.vht.tycho.root ................................. SUCCESS [  0.340 s]
[INFO] com.vht.tycho.bundles .............................. SUCCESS [  0.028 s]
[INFO] com.vht.tycho.plugin1 .............................. SUCCESS [  2.676 s]
[INFO] com.vht.tycho.features ............................. SUCCESS [  0.067 s]
[INFO] com.vht.tycho.feature .............................. SUCCESS [  0.376 s]
[INFO] com.vht.tycho.releng ............................... SUCCESS [  0.023 s]
[INFO] com.vht.tycho.update ............................... SUCCESS [  3.501 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  33.529 s
[INFO] Finished at: 2019-03-27T00:39:15+05:30
[INFO] ------------------------------------------------------------------------
```
Press F5 in the Eclipse IDE on update site project to refresh it. A new target folder in project with repository folder. This folder contains the update site.

Validate that the JAR files in the "repository" have the SNAPSHOT suffix replaced with the build qualifier. And final target/build generation will look like this,
```
main directory
	| --  releng 
		| -- com.vht.tycho.configuration
			| -- pom.xml
		| -- com.vht.tycho.update
			| -- pom.xml
			| -- target
				| -- repository
					| -- features
						| -- com.vht.tycho.feature_1.0.0.201903261908.jar
					| -- plugins
						| -- com.vht.tycho.plugin1_1.0.0.201903261908.jar
					| -- artifacts.jar
					| -- content.jar
				| -- p2agent
				| -- targetPlatformRepository
		| -- pom.xml
	| --  bundles 
		| -- com.vht.tycho.plugin1
		| -- pom.xml
	| --  features 
		| --  com.vht.tycho.feature
		| -- pom.xml
	| -- pom.xml

```
## Tycho build for products

The following exercise demonstrates how to build Eclipse products with Maven Tycho.

### Creating a plug-in and the product project

File -> New -> Other… ->  Plug-in Development -> Plug-in Project menu entry and created a plug-in in the bundles folder. Named this plug-in com.vht.tycho.rcp and used the Eclipse 4 RCP application template. 

Note: To create a  Eclipse 4 RCP application template select 'yes' radio button for Rich Client Application section and click on continue. Here select 'Eclipse 4 RCP application' in available plug-in templates section and click on finish.

Created a new project of type General called com.vht.tycho.product in the releng folder. Moved .product file from the com.vht.tycho.rcp file into this new project.

### Enter ID in the product

To build this product with Tycho, we need to enter the ID on the product configuration file.

### Create poms

No need to create a pom file for com.vht.tycho.rcp plug-in as the default pom is sufficient. Default pom for products are not yet supported, therefore create a pom file for your product.
```
<project>
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>com.vht.tycho</groupId>
		<artifactId>com.vht.tycho.releng</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</parent>
	<groupId>com.vht.tycho</groupId>
	<artifactId>com.vht.tycho.product</artifactId>
	<packaging>eclipse-repository</packaging>
	<version>1.0.0-SNAPSHOT</version>
	<build>
		<plugins>
			<plugin>
				<groupId>org.eclipse.tycho</groupId>
				<artifactId>tycho-p2-repository-plugin</artifactId>
				<version>${tycho.version}</version>
				<configuration>
					<includeAllDependencies>true</includeAllDependencies>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.eclipse.tycho</groupId>
				<artifactId>tycho-p2-director-plugin</artifactId>
				<version>${tycho.version}</version>
				<executions>
					<execution>
						<id>materialize-products</id>
						<goals>
							<goal>materialize-products</goal>
						</goals>
					</execution>
					<execution>
						<id>archive-products</id>
						<goals>
							<goal>archive-products</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
 	</build>
</project>
```

### Run the build

Run the build from the main directory via `mvn clean verify`. This works fine and generates build.
