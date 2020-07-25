import java.util.*;

public class Graph {

	private Map<Integer, List<Integer>> g;
	
	public Graph(List<Edge> edges) {
		g = new HashMap<>();
		for (Edge e : edges) {
			int a = e.n1, b = e.n2;
			if (!g.containsKey(a)) g.put(a, new LinkedList<>());
			if (!g.containsKey(b)) g.put(b, new LinkedList<>());
			g.get(a).add(b);
			g.get(b).add(a);
		}
	}
	public Graph(Map<Integer, List<Integer>> graph) {
		g = new HashMap<>(graph);
	}
	
	public void addEdge(int n, int e) {
		g.get(n).add(e);
	}
	public int getSize() {
		return g.size();
	}
	public List<Integer> getEdges(int n) {
		return new LinkedList<>(g.get(n));
	}
	public Set<Integer> getNodes() {
		return g.keySet();
	}
	
	public static Graph makeRegularGraph(int n, int r, boolean connected, boolean multigraph) {
		Graph g;
		do
			g = makeRegularGraph(n, r);
		while ((connected ? !isConnected(g) : false) && (multigraph ? isMultigraph(g, r) : false));
		return g;
	}
	private static Graph makeRegularGraph(int n, int r) {
		int nr = n * r;
		ArrayList<Integer> points = new ArrayList<>(nr);
		for (int i = 0; i < nr; i++) {
			points.add(i);
		}
		Map<Integer, List<Integer>> g = new HashMap<>();
		for (int i = 0; i < n; i++) {
			g.put(i, new LinkedList<>());
		}
		while (!points.isEmpty()) {
			int a = points.remove(RNG.randInt(0, points.size() - 1)) / r, b = points.remove(RNG.randInt(0, points.size() - 1)) / r;
			g.get(a).add(b);
			g.get(b).add(a);
		}
		return new Graph(g);
	}
	private static boolean isConnected(Graph g) {
		boolean[] visited = new boolean[g.getSize()];
		ArrayDeque<Integer> q = new ArrayDeque<>();
		q.add(0);
		while (!q.isEmpty()) {
			int n = q.poll();
			if (visited[n]) continue;
			visited[n] = true;
			q.addAll(g.getEdges(n));
		}
		for (boolean b : visited)
			if (!b) return false;
		return true;
	}
	private static boolean isMultigraph(Graph g, int r) {
		for (int n : g.getNodes()) {
			HashSet<Integer> s = new HashSet<>(g.getEdges(n));
			if (s.size() != r || s.contains(n)) return true;
		}
		return false;
	}
	
	public static class Edge {
		public final int n1, n2, w;
		
		public Edge(int node1, int node2, int weight) {
			n1 = node1;
			n2 = node2;
			w = weight;
		}
	}
	
}
