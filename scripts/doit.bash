for var in "$@"
do
    version=$(echo $var | awk -F "." '{print $7}')
    java -jar ~/eclipse-workspace/parser/target/parser-0.0.1-SNAPSHOT.jar $version $var/schemas/*
done
