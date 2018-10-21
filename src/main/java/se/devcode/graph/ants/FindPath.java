package se.devcode.graph.ants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tools.ant.taskdefs.Concat;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
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
		skapaTransport(graph, centralstationen, södertälje, 38, 15, "pendeltåg 41");

		System.out.println(jakobsberg);
		System.out.println(jakobsberg.keys());
		jakobsberg.edges(Direction.BOTH).forEachRemaining(t -> System.out.println(t));

		findPath(graph, jakobsberg, södertälje, 100, 5);
	}

	private static void findPath(TitanGraph graph, TitanVertex a, TitanVertex b, int ticks, int nAnts) {
		ArrayList<Ant> ants = new ArrayList<>();
		for (int i = 0; i < nAnts; i++) {
			ants.add(new Ant(a));
		}

		for (int tick = 0; tick < ticks; tick++) {
			tick(graph, ants);
		}

	}

	private static void tick(TitanGraph graph, ArrayList<Ant> ants) {
		UnmodifiableIterator<Vertex> transports = Iterators.filter(graph.vertices(), arg0 -> {
			if (arg0 instanceof TitanVertex) {
				TitanVertex tVertex = (TitanVertex) arg0;
				return Objects.equals(tVertex.property("typ").value(), "transport");
			}
			return false;
		});

		transports.forEachRemaining(t -> {
			Integer lff = (Integer) t.property("lff").value();
			Integer ff = (Integer) t.property("ff").value();

			t.property("lff", lff <= 0 ? 0 : lff--);
			t.property("ff", ff <= 0 ? 0 : ff--);
		});

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
				Iterator<CalculatingUnit> inCUs = Iterators.transform(t.getPosition().edges(Direction.IN),
						arg0 -> new CalculatingUnit(arg0, Direction.IN));
				Iterator<CalculatingUnit> outCUs = Iterators.transform(t.getPosition().edges(Direction.OUT),
						arg0 -> new CalculatingUnit(arg0, Direction.OUT));
				
				Iterator<CalculatingUnit> x = Iterators.concat(inCUs, outCUs);
				
				ArrayList<CalculatingUnit> cus = Lists.newArrayList(x);
				
				if (t.getMode() == Mode.LFF)
				{
					cus.forEach(cu -> cu.modifyProbabilities(t.getMode()));
				}
				
			}
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
