package com.recall.be;

import com.syncleus.ferma.VertexFrame;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;

public class GraphTraversalToMany<FROM, TO> extends DefaultGraphTraversal<FROM, TO> {

    public GraphTraversalToMany() {
        super();
    }

    public GraphTraversalToMany(final GraphTraversalSource graphTraversalSource) {
        super(graphTraversalSource);
    }

    public GraphTraversalToMany(final Graph graph) {
        super(graph);
    }

    public GraphTraversalToSingle<FROM, TO> toSingle() {
        Graph g = getGraph().orElseThrow(() -> new IllegalStateException("Traversal should have a graph"));
        GraphTraversal<FROM, FROM> t = new GraphTraversalToSingle<>(g);
        return (GraphTraversalToSingle<FROM, TO>) t.flatMap(this);
    }

    public GraphTraversalToOptional<FROM, TO> toOptional() {
        Graph g = getGraph().orElseThrow(() -> new IllegalStateException("Traversal should have a graph"));
        GraphTraversal<FROM, FROM> t = new GraphTraversalToOptional<>(g);
        return (GraphTraversalToOptional<FROM, TO>) t.flatMap(this);
    }

    public GraphTraversalToOptional<FROM, TO> hasId(Object id) {
        return (GraphTraversalToOptional<FROM, TO>) toOptional().hasId(id);
    }

    @Override
    public GraphTraversalToMany<FROM, TO> has(String propertyKey, Object value) {
        return (GraphTraversalToMany<FROM, TO>) super.has(propertyKey, value);
    }
}
