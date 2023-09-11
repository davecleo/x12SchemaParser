# x12SchemaParser
This project is designed to automatically generate code tables and surrounding business processes to go between X12 IDs and Descriptions. To make it work you need to:
1.  Generate the X12 schema files for all EDI versions from CIC Studio
2.  Check out the java project into eclipse and build it to product a jar
3.  Copy all of the com.extol.ebi.edi.standard.x12.v00<version>.schemas_3.1.0.201407301516 directories downloaded by studio into a common place (from below the studio application)
4.  Copy the doit.bash script into the same place
5.  Run ./doit.bash com* in that location. This should generate a set if code table files for translation in each direction. The code tables are split into multiple parts due to java limitations on number of tokens. We will generate a wrapper script for each later in the process
6.  In your CIC workspace, create an empty project called com.labs.x12decoder.v002001. We will use this to generate projects for all the other versions.
7.  Copy the script wrapper.bash and the files DescriptionToID\*txt and IDToDescription\*txt into the base directory of your workspace.
8.  Edit wrapper.bash so that the schemadir variable is set to the directory you copied everything to in step 3
9.  Run ./wrapper.bash in the base directory of your workspace. This should copy the base com.labs.x12decoder.v002001 and create a project for every schema version, copy in the codetables and create wrapper BPS to reference them

NOTE: This procedure has only been tested on Mac OS Ventura 13.5
