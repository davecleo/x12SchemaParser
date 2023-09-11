#!/bin/bash
rootpath=com.cleo.labs.x12decoder.
subpath=/src/com/cleo/labs/x12decoder/
schemadir=/Users/dbrunswick/Documents/Development/schema_parser

function moveit() {
    source=$1
    substitute=$2
    replacement=$3

    target=${source/$substitute/$replacement}
    mv $source $target
}

# replicate the directory structure for the appropriate X12 version
function replicate() {
    source=$1$2
    destination=$1$3
    cp -r $source $destination
    for f in `find $destination -name \*$2\*` ; do moveit $f $2 $3 ; done
}

# fix any references to the base version to the new version on the file systems
function fixrefs() {
    grep -rl $2 $1 | xargs sed -i'' -e "s/$2/$3/g"
# remove the backup files that sed creates
    find $1 -name \*-e -exec rm {} \;
}

# go through each of the version types and do the business
for var in `find $schemadir -name com\* -maxdepth 1 -print`
do
    version=$(echo $var | awk -F "." '{print $7}')
# v002001 is what we are copying, so no need to replicate it
    if [ "$version" != "v002001" ]
    then
        replicate $rootpath v002001 $version
    fi
    fullpath=$rootpath$version$subpath$version
    cp IDToDescriptionTop.txt $fullpath/IDToDescriptionBPS.bps
    cp DescriptionToIDTop.txt $fullpath/DescriptionToIDBPS.bps
    numfiles=`find $schemadir -name ID\*$version\* -maxdepth 1 -print | wc -l`
    for (( c=0; c< $numfiles ; c++ ))
    do
        cat IDToDescriptionLogic.txt | sed "s/v002001/$version/g" | sed "s/_0/_$c/g" >> $fullpath/IDToDescriptionBPS.bps
        cat DescriptionToIDLogic.txt | sed "s/v002001/$version/g" | sed "s/_0/_$c/g" >> $fullpath/DescriptionToIDBPS.bps
    done
    cat IDToDescriptionBottom.txt >> $fullpath/IDToDescriptionBPS.bps
    cat DescriptionToIDBottom.txt >> $fullpath/DescriptionToIDBPS.bps
    fixrefs $rootpath$version v002001 $version

done
for var in `find $schemadir -name com\* -maxdepth 1 -print`
do
    version=$(echo $var | awk -F "." '{print $7}')
    fullpath=$rootpath$version$subpath$version
# copy in all the code tables
# I know, redundant code with the above, but can't spend any more time on this
    find $schemadir -name ID\*$version\* -maxdepth 1 -exec cp {} $fullpath \;
    find $schemadir -name Description\*$version\* -maxdepth 1 -exec cp {} $fullpath \;
done

