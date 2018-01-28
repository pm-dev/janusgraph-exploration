package com.recall.be;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;

public class GraphTraversalToSingle<FROM, TO> extends DefaultGraphTraversal<FROM, TO> {

    public GraphTraversalToSingle() {
        super();
    }

    public GraphTraversalToSingle(final GraphTraversalSource graphTraversalSource) {
        super(graphTraversalSource);
    }

    public GraphTraversalToSingle(final Graph graph) {
        super(graph);
    }

    public GraphTraversalToOptional<FROM, TO> toOptional() {
        Graph g = getGraph().orElseThrow(() -> new IllegalStateException("Traversal should have a graph"));
        GraphTraversal<FROM, FROM> t = new GraphTraversalToOptional<>(g);
        return (GraphTraversalToOptional<FROM, TO>) t.map(this);
    }

    @Override
    public GraphTraversalToOptional<FROM, TO> has(String propertyKey, Object value) {
        return toOptional().has(propertyKey, value);
    }
}
