package com.recall.be;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;

public class GraphTraversalToOptional<FROM, TO> extends DefaultGraphTraversal<FROM, TO> {

    public GraphTraversalToOptional() {
        super();
    }

    public GraphTraversalToOptional(final GraphTraversalSource graphTraversalSource) {
        super(graphTraversalSource);
    }

    public GraphTraversalToOptional(final Graph graph) {
        super(graph);
    }

    public GraphTraversalToSingle<FROM, TO> toSingle() {
        Graph g = getGraph().orElseThrow(() -> new IllegalStateException("Traversal should have a graph"));
        GraphTraversal<FROM, FROM> t = new GraphTraversalToSingle<>(g);
        return (GraphTraversalToSingle<FROM, TO>) t.map(this);
    }

    @Override
    public GraphTraversalToOptional<FROM, TO> has(String propertyKey, Object value) {
        return (GraphTraversalToOptional<FROM, TO>) super.has(propertyKey, value);
    }
}
