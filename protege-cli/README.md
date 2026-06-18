# Protege CLI

Headless ontology commands for automation and AI agents.

## Build

```powershell
mvn -gs .\.mvn-settings-protege.xml -s .\.mvn-settings-protege.xml -pl protege-cli -am "-Dmaven.javadoc.skip=true" package
```

The executable jar is:

```text
protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar
```

## Commands

```powershell
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar inspect .\path\to\ontology.owl
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar validate .\path\to\ontology.owl
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar profile .\path\to\ontology.owl
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar signature .\path\to\ontology.owl
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar usages .\path\to\ontology.owl --iri "http://example.com#Entity"
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar convert .\path\to\ontology.owl --to ttl --out .\out\ontology.ttl
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar reason .\path\to\ontology.owl --class "http://example.com#Class"
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar explain .\path\to\ontology.owl --iri "http://example.com#Entity"
java -jar .\protege-cli\target\protege-cli-5.6.10-SNAPSHOT.jar patch .\path\to\ontology.owl --rename-from "http://example.com#Old" --rename-to "http://example.com#New" --out .\out\renamed.owl
```

All command results are JSON so they can be consumed by scripts, tools, and AI agents.

Supported conversion targets:

```text
rdfxml, ttl, owlxml, functional
```

## AI-oriented commands

`reason` uses the OWL API structural reasoner. It reports consistency, unsatisfiable classes, and optionally the direct subclasses, direct superclasses, and equivalent classes for a named class.

`explain` returns a syntactic support packet: matching entities, directly relevant asserted axioms, profile violations that touch those axioms, and structural class hierarchy evidence when the IRI denotes a class. It is not a minimal logical proof.

`patch` currently supports safe rename automation. Without `--out` it is a dry run and reports the affected entities and change count. With `--out` it writes the renamed ontology to a new file and leaves the source file untouched.
