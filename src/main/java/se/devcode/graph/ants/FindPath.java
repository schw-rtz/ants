package se.devcode.graph.ants;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;

public class FindPath {

	public static void main(String[] args) {
		TitanGraph graph = TitanFactory
				.open("C:/pato/dev/graph/titan-1.0.0-hadoop1/conf/" + "titan-berkeleyje.properties");

		TitanVertex jakobsberg = skapaPlats(graph, "Jakobsberg");
		TitanVertex sundbyberg = skapaPlats(graph, "Sundbyberg");
		TitanVertex islandstorget = skapaPlats(graph, "Islandstorget");
		TitanVertex råcksta = skapaPlats(graph, "Råcksta");
		TitanVertex alvik = skapaPlats(graph, "Alvik");
		TitanVertex stadshagen = skapaPlats(graph, "Stadshagen");
		TitanVertex fridhemsplan = skapaPlats(graph, "Fridhemsplan");
		TitanVertex rådmansgatan = skapaPlats(graph, "Rådmansgatan");
		TitanVertex tc = skapaPlats(graph, "T-C");
		TitanVertex centralstationen = skapaPlats(graph, "Centralstationen");
		TitanVertex liljeholmen = skapaPlats(graph, "Liljeholmen");
		TitanVertex södertälje = skapaPlats(graph, "Södertälje");

		skapaTransport(graph, jakobsberg, sundbyberg, 12, 7, "Pendeltåg 40");
		skapaTransport(graph, sundbyberg, islandstorget, 20, 13, "Buss 113");
		skapaTransport(graph, råcksta, islandstorget, 3, 5, "Tunnelbana grön");
		skapaTransport(graph, islandstorget, alvik, 11, 5, "Tunnelbana grön");
		skapaTransport(graph, sundbyberg, stadshagen, 7, 7, "Tunnelbana blå");
		skapaTransport(graph, stadshagen, fridhemsplan, 1, 7, "Tunnelbana blå");
		skapaTransport(graph, fridhemsplan, rådmansgatan, 1, 3, "Tunnelbana grön");
		skapaTransport(graph, sundbyberg, tc, 9, 7, "pendeltåg 40");
		skapaTransport(graph, alvik, liljeholmen, 11, 8, "Tvärbana");
		skapaTransport(graph, alvik, fridhemsplan, 5, 3, "Tunnelbana grön");
		skapaTransport(graph, liljeholmen, fridhemsplan, 15, 13, "Buss 77");
		skapaTransport(graph, tc, liljeholmen, 5, 3, "Tunnelbana röd");
		skapaTransport(graph, tc, centralstationen, 5, 0, "Gång");
		skapaTransport(graph, centralstationen, södertälje, 22, 20, "sj tåg");
		// skapaTransport(graph, centralstationen, södertälje, 38, 15, "pendeltåg 41");

		findPath(graph, jakobsberg, islandstorget, 100, 15);

		printStatus(graph);
	}

	private static void printStatus(TitanGraph graph) {
		Iterators.filter(graph.vertices(), arg0 -> {
			if (arg0 instanceof TitanVertex) {
				TitanVertex tVertex = (TitanVertex) arg0;
				return Objects.equals(tVertex.property("typ").value(), "plats");
			}
			return false;
		}).forEachRemaining(p -> print(p));
	}

	private static void print(Vertex p) {
		StringBuffer result = new StringBuffer();

		result.append("från: ").append(p.property("namn").value()).append("\n");

		Map<String, Edge> incomingEdgesBySourceName = Lists.newArrayList(p.edges(Direction.IN)).stream()
				.collect(Collectors.toMap(
						t1 -> (String) t1.outVertex().edges(Direction.IN).next().outVertex().property("namn").value(),
						e -> e));
		Map<String, Edge> outgoingEdgesByTargetName = Lists.newArrayList(p.edges(Direction.OUT)).stream()
				.collect(Collectors.toMap(
						t1 -> (String) t1.inVertex().edges(Direction.OUT).next().inVertex().property("namn").value(),
						e -> e));

		Set<String> adjecentPlaceNames = Sets.union(incomingEdgesBySourceName.keySet(),
				outgoingEdgesByTargetName.keySet());

		adjecentPlaceNames.forEach(tillplats -> {
			result.append("  till ").append(tillplats).append(" ");

			Integer inLFF = (Integer) incomingEdgesBySourceName.get(tillplats).outVertex().property("lff").value();
			Integer inFF = (Integer) incomingEdgesBySourceName.get(tillplats).outVertex().property("ff").value();
			Integer outLFF = (Integer) outgoingEdgesByTargetName.get(tillplats).inVertex().property("lff").value();
			Integer outFF = (Integer) outgoingEdgesByTargetName.get(tillplats).inVertex().property("ff").value();

			int x = inFF + inLFF + outFF + outLFF;

			result.append(x).append("\n");
		});

		System.out.println(result.toString());
	}

	private static void findPath(TitanGraph graph, TitanVertex a, TitanVertex b, int ticks, int nAnts) {
		ArrayList<Ant> ants = new ArrayList<>();
		for (int i = 0; i < nAnts; i++) {
			ants.add(new Ant(i, a));
		}

		for (int tick = 0; tick < ticks; tick++) {
			tick(tick, graph, ants, a, b);
		}

	}

	private static void tick(int tick, TitanGraph graph, ArrayList<Ant> ants, TitanVertex start, TitanVertex goal) {
		if (tick % 3 == 0) {
			decreaseOdor(graph, ants);
		}

		if (tick % 13 == 0) {
			System.out.println(ants);
			printStatus(graph);
		}
		ants.forEach(t -> {
			if (t.getWaittime() > 0) {
				t.decreaseWait();
				return;
			}

			if (t.isOnTransport()) {
				t.makeItSmell();
				Vertex x = t.getPosition().edges(Direction.OUT).next().inVertex();
				t.setPosition((TitanVertex) x);
				return;
			}

			if (t.isInAPlace()) {
				Vertex aPlace = t.getPosition();
				if (Objects.equals(aPlace, start)) {
					t.setMode(Mode.LFF);
				}

				if (Objects.equals(aPlace, goal)) {
					t.setMode(Mode.FF);
				}

				Map<String, Edge> incomingEdgesBySourceName = Lists.newArrayList(aPlace.edges(Direction.IN)).stream()
						.collect(Collectors.toMap(t1 -> (String) t1.outVertex().edges(Direction.IN).next().outVertex()
								.property("namn").value(), e -> e));
				Map<String, Edge> outgoingEdgesByTargetName = Lists.newArrayList(aPlace.edges(Direction.OUT)).stream()
						.collect(Collectors.toMap(t1 -> (String) t1.inVertex().edges(Direction.OUT).next().inVertex()
								.property("namn").value(), e -> e));

				Set<String> adjecentPlaceNames = Sets.union(incomingEdgesBySourceName.keySet(),
						outgoingEdgesByTargetName.keySet());

				Map<String, CalculatingUnit> calculatingUnitsByAdjecentPlaceName = adjecentPlaceNames.stream() //
						.map(name -> new CalculatingUnit(name, incomingEdgesBySourceName.get(name),
								outgoingEdgesByTargetName.get(name))) //
						.collect(Collectors.toMap(cu -> cu.getName(), cu -> cu));

				calculatingUnitsByAdjecentPlaceName.values().forEach(cu -> cu.modifyProbabilities(t.getMode()));

				CalculatingUnit selectedExit = CalculatingUnit.randomizeAnExit(calculatingUnitsByAdjecentPlaceName);

				Vertex nextTransport = selectedExit.getAdjecentTransport();

				Integer tid = (Integer) nextTransport.property("tid").value();
				Integer frekvens = (Integer) nextTransport.property("frekvens").value();

				Integer waitTime = tid;
				if (0 < frekvens) {
					waitTime = waitTime + new Random().nextInt(frekvens);
				}

				t.setPosition(nextTransport);
				t.setWaittime(waitTime);
			}
		});
	}

	private static void decreaseOdor(TitanGraph graph, ArrayList<Ant> ants) {
		UnmodifiableIterator<Vertex> transports = Iterators.filter(graph.vertices(),
				tVertex -> Objects.equals(tVertex.property("typ").value(), "transport"));

		transports.forEachRemaining(t -> {
			Integer lff = (Integer) t.property("lff").value();
			Integer ff = (Integer) t.property("ff").value();

			t.property("lff", lff <= 0 ? 0 : lff--);
			t.property("ff", ff <= 0 ? 0 : ff--);
		});
	}

	private static TitanVertex skapaPlats(TitanGraph graph, String string) {
		TitanVertex sundbyberg = graph.addVertex();
		sundbyberg.property("namn", string);
		sundbyberg.property("typ", "plats");
		return sundbyberg;
	}

	private static void skapaTransport(TitanGraph graph, TitanVertex a, TitanVertex b, int tid, int frekvens,
			String namn) {
		TitanVertex dit = graph.addVertex();
		dit.property("typ", "transport");
		dit.property("tid", tid);
		dit.property("frekvens", frekvens);
		dit.property("namn", namn);
		dit.property("lff", 0);
		dit.property("ff", 0);

		a.addEdge("resa", dit);
		dit.addEdge("resa", b);

		TitanVertex tillbaka = graph.addVertex();
		tillbaka.property("typ", "transport");
		tillbaka.property("tid", tid);
		tillbaka.property("frekvens", frekvens);
		tillbaka.property("namn", namn);
		tillbaka.property("lff", 0);
		tillbaka.property("ff", 0);

		b.addEdge("resa", tillbaka);
		tillbaka.addEdge("resa", a);
	}

}
