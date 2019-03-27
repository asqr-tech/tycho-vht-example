# Maven Tycho Build

## Enable pomless Tycho build:

### .mvn/extensions.xml:

extensions.xml file helps to build projects without pom files. The content of the .mvn/extensions.xml must look like the following to enable pomless builds.

```
<extensions>
  <extension>
    <groupId>org.eclipse.tycho.extras</groupId>
    <artifactId>tycho-pomless</artifactId>
    <version>1.3.0</version>
  </extension>
</extensions>
```

## Create pom for the build configuration

Created releng directory in main project directory and a new project com.vht.tycho.configuration in releng directory. Created a pom.xml file in com.vht.tycho.configuration. 

#### Project structure:

```
main directory
	| --  releng
		| -- com.vht.tycho.configuration
			| -- pom.xml
```

### Create pom in the root project

Create the following pom.xml file in the root project of your build.

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
 </modules>
</project>
```

#### Project structure:

```
main directory
	| --  releng 
		| -- com.vht.tycho.configuration
			| -- pom.xml
	| -- pom.xml
```

Run the build via mvn clean verify, this build should be successful.

## Tycho build for plug-ins

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
