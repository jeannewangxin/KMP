package algorithms;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import supportGUI.*;

public class DefaultTeam {

	public ArrayList<Point> goulton(ArrayList<Point> points, int edgeThreshold) {
		// goulton 92.97

		ArrayList<Point> fvs = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();

		Evaluation evaluation = new Evaluation();
		while (!evaluation.isValid(reste, fvs, 100)) {
			int max = 0;
			int maxP = 0;
			for (int i = 0; i < reste.size(); i++) {

				int population = degree(reste.get(i), reste, 100);
				if (population > max) {
					max = population;
					maxP = i;
				}
			}
			fvs.add(reste.get(maxP));
			reste.remove(reste.get(maxP));
		}

		return fvs;
	}

	public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> result = (ArrayList<Point>) points.clone();

		for (int i = 0; i < 10; i++) {
			ArrayList<Point> fvs = localSearch(greedy(points, edgeThreshold), points, edgeThreshold);

			System.out.println("MAIN. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

			if (fvs.size() < result.size())
				result = fvs;
		}
		System.out.println("MAIN. result: " + result.size() );

		return result;
	}

	private ArrayList<Point> greedy(ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> points = (ArrayList<Point>) pointsIn.clone();
		ArrayList<Point> result = (ArrayList<Point>) pointsIn.clone();

		for (int i = 0; i < 3; i++) {
			Collections.shuffle(points, new Random(System.nanoTime()));
			ArrayList<Point> rest = removeDuplicates(points);
			ArrayList<Point> fvs = new ArrayList<Point>();

			while (!isSolution(fvs, points, edgeThreshold)) {
				Point choosenOne = rest.get(0);
				for (Point p : rest)
					if (degree(p, rest, edgeThreshold) > degree(choosenOne, rest, edgeThreshold))
						choosenOne = p;
				fvs.add(choosenOne);
				rest.removeAll(fvs);
			}

			if (fvs.size() < result.size())
				result = fvs;

		}

		return result;
	}

	private ArrayList<Point> localSearch(ArrayList<Point> firstSolution, ArrayList<Point> points, int edgeThreshold) {

		// firstSolution = solution of greedy
		ArrayList<Point> current = removeDuplicates(firstSolution);
		// next = 去掉重复之后的 firstSolution
		ArrayList<Point> next = (ArrayList<Point>) current.clone();

		System.out.println("LS. First sol(Solution of greedy): " + current.size());

		do {
			current = next;
			next = remove2add1(current, points, edgeThreshold);
			System.out.println("LS. Current sol: " + current.size() + ". Found next sol: " + next.size());
		} while (score(current) > score(next));// 当current的size > next的size的时候，current = next;

		/*do {
			current = next;
			next = remove3add2(current, points, edgeThreshold);
			System.out.println("LS. remove3add2 Current sol: " + current.size() + ". Found next sol: " + next.size());
		} while (score(current) > score(next));// 当current的size > next的size的时候，current = next;
*/
		System.out.println("LS. Last sol: " + current.size());
		return next;

		// return current;
	}

	private ArrayList<Point> remove2add1(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> test = removeDuplicates(candidate);
		long seed = System.nanoTime();
		Collections.shuffle(test, new Random(seed));// test = 把原来的solution去重，打乱
		ArrayList<Point> rest = removeDuplicates(points);// 所有的点去重
		rest.removeAll(test); // rest = 去掉solution的点

		for (int i = 0; i < test.size(); i++) {
			for (int j = i + 1; j < test.size(); j++) {
				Point q = test.remove(j);
				Point p = test.remove(i);
				// 去掉两个点试试看
				for (Point r : rest) {
					if (r.distance(q) <= 3.85 * edgeThreshold && r.distance(p) <= 3.85 * edgeThreshold) {
						test.add(r);
						if (isSolution(test, points, edgeThreshold))
							return test;
						test.remove(r);
					}
				}
				test.add(i, p);
				test.add(j, q);
			}
		}

		return candidate;
	}

	private ArrayList<Point> remove3add2(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> test = removeDuplicates(candidate);
		long seed = System.nanoTime();
		Collections.shuffle(test, new Random(seed));// test = 把原来的solution去重，打乱
		ArrayList<Point> rest = removeDuplicates(points);// 所有的点去重
		rest.removeAll(test); // rest = 去掉solution的点

		for (int i = 0; i < test.size(); i++) {
			for (int j = i + 1; j < test.size(); j++) {
				for (int m = j + 1; m < test.size(); m++) {
					if (m == test.size())
						break;
					if (degree(test.get(m), points, edgeThreshold) < 2)
						continue;
					if (degree(test.get(j), points, edgeThreshold) < 2)
						continue;
					if (degree(test.get(i), points, edgeThreshold) < 2)
						continue;
					Point z = test.remove(m);
					Point q = test.remove(j);
					Point p = test.remove(i);
					if (z.distance(q) <= 5 * edgeThreshold && z.distance(p) <= 5 * edgeThreshold
							&& p.distance(q) <= 5 * edgeThreshold) {

						// 去掉两个点试试看
						ArrayList<Point> newrest = new ArrayList<Point>();
						for (Point x : rest) {
							if (x.distance(q) <= edgeThreshold || x.distance(p) <= edgeThreshold
									|| x.distance(z) <= edgeThreshold) {
								newrest.add(x);
							}
						}
						// System.out.println("New rest ="+newrest.size());
						for (int r = 0; r < newrest.size(); r++) {
							for (int k = r + 1; k < newrest.size(); k++) {
								// if(rest.get(r).distance(q)<= 2.85*edgeThreshold && rest.get(r).distance(p)<=
								// 2.85*edgeThreshold && rest.get(r).distance(z)<= 2.85*edgeThreshold &&
								// rest.get(k).distance(q)<= 2.85*edgeThreshold && rest.get(k).distance(p)<=
								// 2.85*edgeThreshold && rest.get(k).distance(z)<= 2.85*edgeThreshold) {
								test.add(rest.get(r));
								test.add(rest.get(k));
								if (isSolution(test, points, edgeThreshold))
									return test;
								test.remove(rest.get(r));
								test.remove(rest.get(k));
								// }
							}
						}
					}
					test.add(i, p);
					test.add(j, q);
					test.add(m, z);

				}
			}

		}
		return candidate;
	}

	private boolean isSolution(ArrayList<Point> candidateIn, ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> candidate = removeDuplicates(candidateIn);
		ArrayList<Point> rest = removeDuplicates(pointsIn);
		rest.removeAll(candidate);
		ArrayList<Point> visited = new ArrayList<Point>();

		while (!rest.isEmpty()) {
			visited.clear();
			visited.add(rest.remove(0));
			for (int i = 0; i < visited.size(); i++) {
				for (Point p : rest)
					if (isEdge(visited.get(i), p, edgeThreshold)) {
						for (Point q : visited)
							if (!q.equals(visited.get(i)) && isEdge(p, q, edgeThreshold))
								return false;
						visited.add(p);
					}
				rest.removeAll(visited);
			}
		}

		return true;
	}

	private ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
		ArrayList<Point> result = (ArrayList<Point>) points.clone();
		for (int i = 0; i < result.size(); i++) {
			for (int j = i + 1; j < result.size(); j++)
				if (result.get(i).equals(result.get(j))) {
					result.remove(j);
					j--;
				}
		}
		return result;
	}

	private boolean isEdge(Point p, Point q, int edgeThreshold) {
		return p.distance(q) < edgeThreshold;
	}

	private int degree(Point p, ArrayList<Point> points, int edgeThreshold) {
		int degree = -1;
		for (Point q : points)
			if (isEdge(p, q, edgeThreshold))
				degree++;
		return degree;
	}

	private int score(ArrayList<Point> candidate) {
		return candidate.size();
	}

	public static void main(String arg[]) throws FileNotFoundException {

		File file = new File("input.points");
		Scanner scanner = new Scanner(file);

		ArrayList<Point> origine = new ArrayList<Point>();

		while (scanner.hasNextInt()) {
			origine.add(new Point(scanner.nextInt(), scanner.nextInt()));
		}
		scanner.close();

		supportGUI.FramedGUI fram = new FramedGUI(1200, 800, null, 0, 100, false);
		fram.drawPoints(origine, origine);

	}
}
