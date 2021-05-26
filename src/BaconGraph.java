import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BaconGraph {
	private static final int DEFAULT_NUMBER_OF_ACTORS = 3000;
	private static final int DEFAULT_NUMBER_OF_MOVIES = 1000;
	private static final double HASHMAP_DEFAULT_LOADFACTOR = 0.75;
	private static final String CENTER_ACTOR = "Bacon, Kevin (I)";

	private final int expectedNumberOfActors;
	private final int expectedNumberOfMovies;

	private final Map<String, Node> actors;

	/**
	 * Creates a graph of actors and movies from provided file, connected if an
	 * actor has appeared in the movie and calculates the shortest path and degree
	 * of separation between all actors and Kevin Bacon. Uses a default expected
	 * number of actors of 3000 and a default expected number of movies of 1000. If
	 * number of expected actors or movies deviates largely from this it is
	 * recommended to explicitly provide expected number of actors and movies when
	 * creating the graph to increase performance, using the
	 * {@link #BaconGraph(String, int, int) other constructor}.
	 * <p>
	 * Prints progress to console during creation. For bigger files, this might take
	 * a couple of seconds.
	 * 
	 * @param file the name of the file containing actors and movies. Expects a
	 *             format where actors are listed on their own rows, beginning with
	 *             the prefix &lt;a&gt;. Followed by rows of movies the actor has
	 *             appeared in beginning with the prefix &lt;t&gt;. All rows not
	 *             beginning with &lt;a&gt; or &lt;t&gt; are ignored.
	 * @throws FileNotFoundException    If the given filename is not a file or the
	 *                                  file can't be read
	 * @throws IllegalArgumentException If provided filename is null
	 * @throws IOException              If an input or output exception occurred
	 *                                  while reading the file
	 * @throws NoBaconException         If the file does not contain an actor with
	 *                                  the name "Bacon, Kevin (I)"
	 */
	public BaconGraph(String file) throws FileNotFoundException, IOException, NoBaconException {
		this(file, DEFAULT_NUMBER_OF_ACTORS, DEFAULT_NUMBER_OF_MOVIES);
	}

	/**
	 * Creates a graph of actors and movies from provided file, connected if an
	 * actor has appeared in the movie and calculates the shortest path and degree
	 * of separation between all actors and Kevin Bacon. Uses provided expected
	 * number of actors and movies to increase performance.
	 * <p>
	 * Prints progress to console during creation. For bigger files, this might take
	 * a couple of seconds.
	 * 
	 * @param file                   the name of the file containing actors and
	 *                               movies. Expects a format where actors are
	 *                               listed on their own rows, beginning with the
	 *                               prefix &lt;a&gt;. Followed by rows of movies
	 *                               the actor has appeared in beginning with the
	 *                               prefix &lt;t&gt;. All rows not beginning with
	 *                               &lt;a&gt; or &lt;t&gt; are ignored.
	 * @param expectedNumberOfActors the expected number of Actors in the provided
	 *                               file
	 * @param expectedNumberOfMovies the expected number of Movies in the provided
	 *                               file
	 * 
	 * @throws FileNotFoundException    If the given filename is not a file or the
	 *                                  file can't be read
	 * @throws IllegalArgumentException If provided filename is null or expected
	 *                                  number of actors or movies is negative.
	 * @throws IOException              If an input or output exception occurred
	 *                                  while reading the file
	 * @throws NoBaconException         If the file does not contain an actor with
	 *                                  the name "Bacon, Kevin (I)"
	 */
	public BaconGraph(String file, int expectedNumberOfActors, int expectedNumberOfMovies)
			throws FileNotFoundException, IOException, NoBaconException {

		if (file == null)
			throw new IllegalArgumentException("File name can't be null");

		if (expectedNumberOfActors < 0 || expectedNumberOfMovies < 0)
			throw new IllegalArgumentException("Expected number of actors and movies can't be negative");

		long startTime = System.currentTimeMillis();

		this.expectedNumberOfActors = expectedNumberOfActors;
		this.expectedNumberOfMovies = expectedNumberOfMovies;
		actors = new HashMap<String, Node>(calculateHashMapCapacity(this.expectedNumberOfActors));

		readFile(file);
		breadthFirstSearch();

		System.out.println("Total Time " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	/**
	 * Calculates an initial capacity for a Hashmap that won't lead to rehashing
	 * when adding the given number of elements. Uses the default loadfactor for
	 * Hashmap to calculate the value.
	 * 
	 * @param expectedNoOfElements the expected number of elements to be added to
	 *                             the map
	 * @return the calculated initial capacity for the hashmap
	 */
	private int calculateHashMapCapacity(int expectedNoOfElements) {
		return (int) (expectedNoOfElements / HASHMAP_DEFAULT_LOADFACTOR + 1);
	}

	private void readFile(String file) throws FileNotFoundException, IOException {
		System.out.println("Reading file...");
		long startTime = System.currentTimeMillis();
		try (BufferedReader reader = new BufferedReader(new FileReader(file));) {

			String line = reader.readLine();
			Node actor = null;
			Map<String, Node> movies = new HashMap<>(calculateHashMapCapacity(expectedNumberOfMovies));
			while (line != null) {
				if (line.startsWith(NodeType.ACTOR.prefix)) {
					line = line.substring(NodeType.ACTOR.prefix.length());
					actor = new Node(line, NodeType.ACTOR);
					actors.put(line, actor);

				} else if (line.startsWith(NodeType.MOVIE.prefix)) {
					line = line.substring(NodeType.MOVIE.prefix.length());
					Node movie = movies.get(line);
					if (movie == null) {
						movie = new Node(line, NodeType.MOVIE);
						movies.put(line, movie);
					}
					actor.adjList.add(movie);
					movie.adjList.add(actor);
				}
				line = reader.readLine();
			}

			System.out.println("File read in " + (System.currentTimeMillis() - startTime) + " milliseconds");
			System.out.println("Actors read: " + actors.size());
			System.out.println("Movies read: " + movies.size());
		}
	}

	private void breadthFirstSearch() throws NoBaconException {
		System.out.println("Calculating Bacon...");
		long startTime = System.currentTimeMillis();
		Queue<Node> q = new LinkedList<Node>();
		Node kevin = actors.get(CENTER_ACTOR);
		if (kevin == null)
			throw new NoBaconException("Kevin Bacon was not found");
		kevin.pathLength = 0;
		q.add(kevin);

		while (!q.isEmpty()) {
			Node n = q.remove();

			for (Node w : n.adjList) {
				if (w.pathLength == Integer.MAX_VALUE) {
					w.pathLength = n.pathLength + 1;
					w.path = n;
					q.add(w);
				}
			}
		}
		System.out.println("Search done in " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	public int getBaconNumber(String actor) {
		Node actorNode = (Node) actors.get(actor);
		if (actorNode == null)
			return -1;
		return actorNode.pathLength == Integer.MAX_VALUE ? actorNode.pathLength : actorNode.pathLength / 2;
	}

	public String getBaconPath(String actor) {
		Node actorNode = getActor(actor);
		StringBuilder stringBuilder = new StringBuilder();
		if (actorNode == null)
			return null;
		return actorNode.getPath(stringBuilder);
	}

	private Node getActor(String name) {
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("Actor can't be null or empty");
		return actors.get(name);
	}

	private enum NodeType {
		MOVIE("<t>"), ACTOR("<a>");

		final String prefix;

		private NodeType(String prefix) {
			this.prefix = prefix;
		}
	}

	private static class Node {
		final String name;
		final NodeType type;
		List<Node> adjList;
		Node path;
		int pathLength = Integer.MAX_VALUE;

		public Node(String name, NodeType type) {
			adjList = new ArrayList<Node>();
			this.name = name;
			this.type = type;
		}

		String getPath(StringBuilder stringBuilder) {
			if (path != null) {
				path.getPath(stringBuilder);
			}
			stringBuilder.append(type.prefix + name + type.prefix);
			return stringBuilder.toString();
		}
	}
}
