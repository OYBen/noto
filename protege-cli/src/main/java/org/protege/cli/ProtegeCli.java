package org.protege.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

public class ProtegeCli {

    private static final ObjectMapper JSON = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) {
        int exitCode = new ProtegeCli().run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private int run(String[] args) {
        if (args.length == 0 || "--help".equals(args[0]) || "-h".equals(args[0])) {
            printHelp();
            return 0;
        }

        String command = args[0];
        Args parsed = Args.parse(Arrays.copyOfRange(args, 1, args.length));
        try {
            Map<String, Object> result = execute(command, parsed);
            writeJson(result);
            return Boolean.FALSE.equals(result.get("ok")) ? 1 : 0;
        } catch (UsageException e) {
            writeError(command, "usage", e.getMessage());
            return 2;
        } catch (OWLOntologyCreationException e) {
            writeError(command, "load-failed", rootMessage(e));
            return 1;
        } catch (Exception e) {
            writeError(command, "failed", rootMessage(e));
            return 1;
        }
    }

    private Map<String, Object> execute(String command, Args args) throws Exception {
        switch (command) {
            case "inspect":
                return inspect(load(args.requireOntology()), command);
            case "validate":
                return validate(args.requireOntology(), command);
            case "profile":
                return profile(load(args.requireOntology()), command);
            case "signature":
                return signature(load(args.requireOntology()), command);
            case "usages":
                return usages(load(args.requireOntology()), command, args.requireOption("iri"));
            case "convert":
                return convert(load(args.requireOntology()), command, args.requireOption("to"), args.requireOption("out"));
            case "reason":
                return reason(load(args.requireOntology()), command, args.option("class"));
            case "explain":
                return explain(load(args.requireOntology()), command, args.requireOption("iri"));
            case "patch":
                return patch(load(args.requireOntology()), command, args);
            default:
                throw new UsageException("Unknown command: " + command);
        }
    }

    private LoadedOntology load(File ontologyFile) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        return new LoadedOntology(manager, ontology, ontologyFile);
    }

    private Map<String, Object> inspect(LoadedOntology loaded, String command) {
        OWLOntology ontology = loaded.ontology;
        Map<String, Object> result = ok(command, loaded.file);
        result.put("ontologyId", ontology.getOntologyID().toString());
        result.put("documentFormat", loaded.manager.getOntologyFormat(ontology).getKey());
        result.put("importsClosureSize", ontology.getImportsClosure().size());
        result.put("directImports", sortedStrings(ontology.getDirectImportsDocuments()));
        result.put("axioms", counts(
                "declared", ontology.getAxiomCount(Imports.EXCLUDED),
                "importsClosure", ontology.getAxiomCount(Imports.INCLUDED),
                "logical", ontology.getLogicalAxiomCount(Imports.INCLUDED)
        ));
        result.put("signature", signatureCounts(ontology));
        result.put("axiomTypes", axiomTypeCounts(ontology));
        return result;
    }

    private Map<String, Object> validate(File file, String command) {
        Map<String, Object> result = base(command, file);
        try {
            LoadedOntology loaded = load(file);
            result.put("ok", true);
            result.put("loaded", true);
            result.put("ontologyId", loaded.ontology.getOntologyID().toString());
            result.put("importsClosureSize", loaded.ontology.getImportsClosure().size());
            result.put("profile", profileSummary(new OWL2DLProfile(), loaded.ontology));
        } catch (OWLOntologyCreationException | OWLRuntimeException e) {
            result.put("ok", false);
            result.put("loaded", false);
            result.put("errorCode", "load-failed");
            result.put("message", rootMessage(e));
        }
        return result;
    }

    private Map<String, Object> profile(LoadedOntology loaded, String command) {
        Map<String, Object> result = ok(command, loaded.file);
        List<Map<String, Object>> profiles = new ArrayList<>();
        for (OWLProfile profile : Arrays.asList(
                new OWL2Profile(),
                new OWL2DLProfile(),
                new OWL2ELProfile(),
                new OWL2QLProfile(),
                new OWL2RLProfile())) {
            profiles.add(profileSummary(profile, loaded.ontology));
        }
        result.put("profiles", profiles);
        return result;
    }

    private Map<String, Object> signature(LoadedOntology loaded, String command) {
        OWLOntology ontology = loaded.ontology;
        Map<String, Object> result = ok(command, loaded.file);
        result.put("counts", signatureCounts(ontology));
        result.put("classes", entityIris(ontology.getClassesInSignature(Imports.INCLUDED)));
        result.put("objectProperties", entityIris(ontology.getObjectPropertiesInSignature(Imports.INCLUDED)));
        result.put("dataProperties", entityIris(ontology.getDataPropertiesInSignature(Imports.INCLUDED)));
        result.put("annotationProperties", entityIris(ontology.getAnnotationPropertiesInSignature(Imports.INCLUDED)));
        result.put("individuals", entityIris(ontology.getIndividualsInSignature(Imports.INCLUDED)));
        return result;
    }

    private Map<String, Object> usages(LoadedOntology loaded, String command, String iriText) {
        IRI iri = IRI.create(iriText);
        Map<String, Object> result = ok(command, loaded.file);
        result.put("iri", iri.toString());

        Set<OWLEntity> entities = new LinkedHashSet<>();
        entities.addAll(loaded.ontology.getEntitiesInSignature(iri, Imports.INCLUDED));

        List<Map<String, Object>> entityResults = new ArrayList<>();
        Set<String> axiomStrings = new LinkedHashSet<>();
        for (OWLEntity entity : entities) {
            Set<OWLAxiom> axioms = loaded.ontology.getReferencingAxioms(entity, Imports.INCLUDED);
            Map<String, Object> entityMap = new LinkedHashMap<>();
            entityMap.put("type", entity.getEntityType().getName());
            entityMap.put("iri", entity.getIRI().toString());
            entityMap.put("axiomCount", axioms.size());
            entityResults.add(entityMap);
            for (OWLAxiom axiom : axioms) {
                axiomStrings.add(axiom.toString());
            }
        }
        for (OWLAxiom axiom : loaded.ontology.getReferencingAxioms(iri, Imports.INCLUDED)) {
            axiomStrings.add(axiom.toString());
        }

        result.put("entities", entityResults);
        result.put("axiomCount", axiomStrings.size());
        result.put("axioms", axiomStrings.stream().sorted().collect(Collectors.toList()));
        return result;
    }

    private Map<String, Object> convert(LoadedOntology loaded, String command, String to, String outPath) throws Exception {
        OWLDocumentFormat format = formatFor(to);
        File out = new File(outPath);
        File parent = out.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new UsageException("Could not create output directory: " + parent.getAbsolutePath());
        }
        loaded.manager.saveOntology(loaded.ontology, format, IRI.create(out.toURI()));
        Map<String, Object> result = ok(command, loaded.file);
        result.put("format", format.getKey());
        result.put("output", out.getAbsolutePath());
        return result;
    }

    private Map<String, Object> reason(LoadedOntology loaded, String command, String classIri) {
        OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(loaded.ontology);
        try {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Map<String, Object> result = ok(command, loaded.file);
            result.put("reasoner", reasoner.getReasonerName());
            result.put("reasonerKind", "structural");
            result.put("consistent", reasoner.isConsistent());
            result.put("unsatisfiableClasses", entityIris(reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom()));

            if (classIri != null) {
                OWLClass cls = loaded.manager.getOWLDataFactory().getOWLClass(IRI.create(classIri));
                result.put("class", cls.getIRI().toString());
                result.put("declared", loaded.ontology.containsClassInSignature(cls.getIRI(), Imports.INCLUDED));
                result.put("directSubClasses", classNodeSetIris(reasoner.getSubClasses(cls, true)));
                result.put("directSuperClasses", classNodeSetIris(reasoner.getSuperClasses(cls, true)));
                result.put("equivalentClasses", entityIris(reasoner.getEquivalentClasses(cls).getEntities()));
            }
            return result;
        } finally {
            reasoner.dispose();
        }
    }

    private Map<String, Object> explain(LoadedOntology loaded, String command, String iriText) {
        IRI iri = IRI.create(iriText);
        Set<OWLEntity> entities = loaded.ontology.getEntitiesInSignature(iri, Imports.INCLUDED);
        Map<String, Object> result = ok(command, loaded.file);
        result.put("iri", iri.toString());
        result.put("explanationType", "syntactic-support");
        result.put("note", "This command returns directly relevant asserted axioms and profile evidence, not a minimal logical proof.");
        result.put("entities", entities.stream().map(this::entityMap).collect(Collectors.toList()));

        Set<OWLAxiom> relevantAxioms = new LinkedHashSet<>();
        for (OWLEntity entity : entities) {
            relevantAxioms.addAll(loaded.ontology.getReferencingAxioms(entity, Imports.INCLUDED));
        }
        relevantAxioms.addAll(loaded.ontology.getReferencingAxioms(iri, Imports.INCLUDED));
        result.put("axiomCount", relevantAxioms.size());
        result.put("axioms", relevantAxioms.stream().map(Object::toString).sorted().collect(Collectors.toList()));
        result.put("profiles", profileEvidence(loaded.ontology, relevantAxioms));

        if (entities.stream().anyMatch(OWLEntity::isOWLClass)) {
            result.put("reasoning", classReasoningEvidence(loaded, iri));
        }
        return result;
    }

    private Map<String, Object> patch(LoadedOntology loaded, String command, Args args) throws Exception {
        String from = args.requireOption("rename-from");
        String to = args.requireOption("rename-to");
        String outPath = args.option("out");
        IRI fromIri = IRI.create(from);
        IRI toIri = IRI.create(to);

        Set<OWLEntity> entities = loaded.ontology.getEntitiesInSignature(fromIri, Imports.INCLUDED);
        if (entities.isEmpty()) {
            throw new UsageException("No entity found for --rename-from: " + from);
        }

        OWLEntityRenamer renamer = new OWLEntityRenamer(loaded.manager, Collections.singleton(loaded.ontology));
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLEntity entity : entities) {
            changes.addAll(renamer.changeIRI(entity, toIri));
        }

        Map<String, Object> result = ok(command, loaded.file);
        result.put("operation", "rename");
        result.put("from", fromIri.toString());
        result.put("to", toIri.toString());
        result.put("entities", entities.stream().map(this::entityMap).collect(Collectors.toList()));
        result.put("changeCount", changes.size());
        result.put("dryRun", outPath == null);

        if (outPath != null) {
            loaded.manager.applyChanges(changes);
            File out = new File(outPath);
            File parent = out.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new UsageException("Could not create output directory: " + parent.getAbsolutePath());
            }
            loaded.manager.saveOntology(loaded.ontology, loaded.manager.getOntologyFormat(loaded.ontology), IRI.create(out.toURI()));
            result.put("output", out.getAbsolutePath());
        }
        return result;
    }

    private OWLDocumentFormat formatFor(String name) {
        switch (name.toLowerCase()) {
            case "rdfxml":
            case "rdf/xml":
            case "owl":
                return new RDFXMLDocumentFormat();
            case "ttl":
            case "turtle":
                return new TurtleDocumentFormat();
            case "owlxml":
            case "owl/xml":
                return new OWLXMLDocumentFormat();
            case "functional":
            case "ofn":
            case "fss":
                return new FunctionalSyntaxDocumentFormat();
            default:
                throw new UsageException("Unsupported format: " + name);
        }
    }

    private Map<String, Object> profileSummary(OWLProfile profile, OWLOntology ontology) {
        OWLProfileReport report = profile.checkOntology(ontology);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", profile.getName());
        map.put("inProfile", report.isInProfile());
        map.put("violationCount", report.getViolations().size());
        map.put("violations", report.getViolations().stream()
                .limit(50)
                .map(this::violationMap)
                .collect(Collectors.toList()));
        return map;
    }

    private List<Map<String, Object>> profileEvidence(OWLOntology ontology, Set<OWLAxiom> relevantAxioms) {
        List<Map<String, Object>> evidence = new ArrayList<>();
        for (OWLProfile profile : Arrays.asList(new OWL2DLProfile(), new OWL2ELProfile(), new OWL2QLProfile(), new OWL2RLProfile())) {
            OWLProfileReport report = profile.checkOntology(ontology);
            List<Map<String, Object>> violations = report.getViolations().stream()
                    .filter(violation -> relevantAxioms.contains(violation.getAxiom()))
                    .limit(25)
                    .map(this::violationMap)
                    .collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", profile.getName());
            map.put("inProfile", report.isInProfile());
            map.put("relevantViolationCount", violations.size());
            map.put("violations", violations);
            evidence.add(map);
        }
        return evidence;
    }

    private Map<String, Object> classReasoningEvidence(LoadedOntology loaded, IRI iri) {
        OWLClass cls = loaded.manager.getOWLDataFactory().getOWLClass(iri);
        OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(loaded.ontology);
        try {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("reasoner", reasoner.getReasonerName());
            map.put("reasonerKind", "structural");
            map.put("satisfiable", reasoner.isSatisfiable(cls));
            map.put("directSubClasses", classNodeSetIris(reasoner.getSubClasses(cls, true)));
            map.put("directSuperClasses", classNodeSetIris(reasoner.getSuperClasses(cls, true)));
            map.put("equivalentClasses", entityIris(reasoner.getEquivalentClasses(cls).getEntities()));
            return map;
        } finally {
            reasoner.dispose();
        }
    }

    private Map<String, Object> violationMap(OWLProfileViolation violation) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", violation.getClass().getSimpleName());
        map.put("message", violation.toString());
        OWLAxiom axiom = violation.getAxiom();
        if (axiom != null) {
            map.put("axiom", axiom.toString());
        }
        return map;
    }

    private Map<String, Object> signatureCounts(OWLOntology ontology) {
        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("classes", ontology.getClassesInSignature(Imports.INCLUDED).size());
        counts.put("objectProperties", ontology.getObjectPropertiesInSignature(Imports.INCLUDED).size());
        counts.put("dataProperties", ontology.getDataPropertiesInSignature(Imports.INCLUDED).size());
        counts.put("annotationProperties", ontology.getAnnotationPropertiesInSignature(Imports.INCLUDED).size());
        counts.put("individuals", ontology.getIndividualsInSignature(Imports.INCLUDED).size());
        counts.put("datatypes", ontology.getDatatypesInSignature(Imports.INCLUDED).size());
        return counts;
    }

    private Map<String, Object> axiomTypeCounts(OWLOntology ontology) {
        Map<String, Object> counts = new LinkedHashMap<>();
        for (AxiomType<?> type : AxiomType.AXIOM_TYPES) {
            int count = ontology.getAxioms(type, Imports.INCLUDED).size();
            if (count > 0) {
                counts.put(type.getName(), count);
            }
        }
        return counts;
    }

    private List<String> entityIris(Collection<? extends OWLEntity> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(entity -> entity.getIRI().toString()))
                .map(entity -> entity.getIRI().toString())
                .collect(Collectors.toList());
    }

    private List<String> classNodeSetIris(NodeSet<OWLClass> nodeSet) {
        return entityIris(nodeSet.getFlattened());
    }

    private Map<String, Object> entityMap(OWLEntity entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", entity.getEntityType().getName());
        map.put("iri", entity.getIRI().toString());
        return map;
    }

    private List<String> sortedStrings(Collection<?> values) {
        return values.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<String, Object> counts(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(String.valueOf(values[i]), values[i + 1]);
        }
        return map;
    }

    private Map<String, Object> ok(String command, File file) {
        Map<String, Object> map = base(command, file);
        map.put("ok", true);
        return map;
    }

    private Map<String, Object> base(String command, File file) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ok", false);
        map.put("command", command);
        map.put("source", file.getAbsolutePath());
        return map;
    }

    private void writeError(String command, String code, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("ok", false);
        error.put("command", command);
        error.put("errorCode", code);
        error.put("message", message);
        writeJson(error);
    }

    private void writeJson(Map<String, Object> result) {
        try {
            System.out.println(JSON.writeValueAsString(result));
        } catch (Exception e) {
            System.err.println(result);
        }
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }

    private void printHelp() {
        System.out.println("Protege CLI");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  protege-cli inspect <ontology>");
        System.out.println("  protege-cli validate <ontology>");
        System.out.println("  protege-cli profile <ontology>");
        System.out.println("  protege-cli signature <ontology>");
        System.out.println("  protege-cli usages <ontology> --iri <IRI>");
        System.out.println("  protege-cli convert <ontology> --to rdfxml|ttl|owlxml|functional --out <file>");
        System.out.println("  protege-cli reason <ontology> [--class <IRI>]");
        System.out.println("  protege-cli explain <ontology> --iri <IRI>");
        System.out.println("  protege-cli patch <ontology> --rename-from <IRI> --rename-to <IRI> [--out <file>]");
    }

    private static final class LoadedOntology {
        private final OWLOntologyManager manager;
        private final OWLOntology ontology;
        private final File file;

        private LoadedOntology(OWLOntologyManager manager, OWLOntology ontology, File file) {
            this.manager = manager;
            this.ontology = ontology;
            this.file = file;
        }
    }

    private static final class Args {
        private final List<String> positional = new ArrayList<>();
        private final Map<String, String> options = new LinkedHashMap<>();

        private static Args parse(String[] args) {
            Args parsed = new Args();
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("--")) {
                    String key = arg.substring(2);
                    if ("json".equals(key)) {
                        parsed.options.put(key, "true");
                        continue;
                    }
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                        throw new UsageException("Missing value for option: " + arg);
                    }
                    parsed.options.put(key, args[++i]);
                } else {
                    parsed.positional.add(arg);
                }
            }
            return parsed;
        }

        private File requireOntology() {
            if (positional.isEmpty()) {
                throw new UsageException("Missing ontology path");
            }
            File file = new File(positional.get(0));
            if (!file.isFile()) {
                throw new UsageException("Ontology file does not exist: " + file.getAbsolutePath());
            }
            return file;
        }

        private String requireOption(String name) {
            String value = options.get(name);
            if (value == null || value.trim().isEmpty()) {
                throw new UsageException("Missing --" + name);
            }
            return value;
        }

        private String option(String name) {
            String value = options.get(name);
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return value;
        }
    }

    private static final class UsageException extends RuntimeException {
        private UsageException(String message) {
            super(message);
        }
    }
}
