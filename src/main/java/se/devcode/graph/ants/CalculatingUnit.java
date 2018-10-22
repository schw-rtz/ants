package se.devcode.graph.ants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CalculatingUnit {

	private String name;
	private double inLFF;
	private double inFF;
	private double outLFF;
	private double outFF;
	private Vertex adjecentTransport;

	public CalculatingUnit(String name, Edge incomingEdge, Edge outgoingEdge) {
		this.name = name;

		Vertex incomingTransport = incomingEdge.outVertex();
		inLFF = (Integer) incomingTransport.property("lff").value();
		inFF = (Integer) incomingTransport.property("ff").value();

		Vertex outgoingTransport = outgoingEdge.inVertex();
		outLFF = (Integer) outgoingTransport.property("lff").value();
		outFF = (Integer) outgoingTransport.property("ff").value();

		adjecentTransport = outgoingTransport;
	}

	public String getName() {
		return name;
	}

	public double getInLFF() {
		return inLFF;
	}

	public double getInFF() {
		return inFF;
	}

	public double getOutLFF() {
		return outLFF;
	}

	public double getOutFF() {
		return outFF;
	}

	public Vertex getAdjecentTransport() {
		return adjecentTransport;
	}

	public void modifyProbabilities(Mode mode) {
		if (mode == Mode.LFF) {
			inLFF = inLFF / 2.0;
			inFF = inFF * 2.0;
		}

		if (mode == Mode.FF) {
			inLFF = inLFF * 2.0;
			inFF = inFF / 2.0;
		}

		outLFF = outLFF / 2.0;
		outFF = outFF / 2.0;
	}

	public static CalculatingUnit randomizeAnExit(Map<String, CalculatingUnit> calculatingUnitsByAdjecentPlaceName) {
		Map<String, Double> sumsByName = calculatingUnitsByAdjecentPlaceName.values().stream()
				.collect(Collectors.toMap(t -> t.getName(), t -> t.getOdorSum()));
		Double totalSum = sumsByName.values().stream().collect(Collectors.summingDouble(value -> value));

		if (totalSum <= 0.0001) {
			return new ArrayList<>(calculatingUnitsByAdjecentPlaceName.values())
					.get(new Random().nextInt(calculatingUnitsByAdjecentPlaceName.values().size()));
		}

		List<PairXY<String, Double>> probabilityList = sumsByName.entrySet().stream() //
				.map(t -> new PairXY<String, Double>(t.getKey(), t.getValue())) //
				.sorted((Comparator<PairXY<String, Double>>) (o1, o2) -> o1.getX().compareTo(o2.getX())) //
				.map(pair -> new PairXY<>(pair.getX(), pair.getY() / totalSum)).collect(Collectors.toList());

		Random random = new Random();
		double[] randomNumber = new double[] { random.nextDouble() };
		ArrayList<PairXY<String, Double>> candidate = new ArrayList<PairXY<String, Double>>();
		probabilityList.forEach(pair -> {
			Double currentProbability = pair.getY();

			if (candidate.isEmpty() && 0 <= randomNumber[0] && randomNumber[0] < currentProbability) {
				candidate.add(pair);
			} else {
				randomNumber[0] = randomNumber[0] - currentProbability;
			}
		});

		return calculatingUnitsByAdjecentPlaceName.get(candidate.iterator().next().getX());
	}

	protected Double getOdorSum() {
		return inFF + inLFF + outFF + outLFF;
	}

}
