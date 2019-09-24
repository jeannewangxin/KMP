package algorithms;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import supportGUI.*;

public class DefaultTeam {

	public ArrayList<Point> init(ArrayList<Point> points) {
		
		ArrayList<Point> fvs = new ArrayList<Point>();
		ArrayList<Point> reste = new ArrayList<Point>();
		reste = points;
		Evaluation evaluation = new Evaluation();
		while (!evaluation.isValid(reste, fvs, 100)) {
			int max = 0;
			int maxP = 0;
			for (int i = 0; i < points.size(); i++) {

				int population = evaluation.getPopulation(points.get(i), points, 100);
				if (population > max) {
					max = population;
					maxP = i;
				}
			}
			fvs.add(points.get(maxP));
			reste.remove(points.get(maxP));
		}
		System.out.println("Size = " + fvs.size());
		return fvs;
	}

	public double distance(Point p1, Point p2) {
		return Math.sqrt(
				(p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}
	
	private  ArrayList<Point> replace(ArrayList<Point> fvs, ArrayList<Point> reste,ArrayList<Point> origine){
		
		ArrayList<Point> candidat = new ArrayList<Point>();
		ArrayList<Point> candidat_rest = new ArrayList<Point>();
		Evaluation evaluation = new Evaluation();
		for (Point point : reste) {
			for (Point p : fvs) {
				for (Point q : fvs) {
					if (evaluation.isneighbor(point, p, origine, 100)
							&& evaluation.isneighbor(point, q, origine, 100) && !p.equals(q)) {
						candidat.remove(p);
						candidat_rest.add(p);
						candidat.remove(q);
						candidat_rest.add(q);
						candidat.add(point);
						candidat_rest.remove(point);
						if(evaluation.isValid(candidat_rest, candidat, 100)) {
							System.out.println("Size candidat = " + candidat.size());
							return candidat;
						}
						else {
							candidat = fvs;
							candidat_rest = reste;
						}
					}
				}
			}
		}
		return fvs;
	}

	private ArrayList<Point> localSearch(ArrayList<Point> fvs, ArrayList<Point> reste,ArrayList<Point> origine) {

		ArrayList<Point> current = new ArrayList<Point>();
		ArrayList<Point> candidat = new ArrayList<Point>();
		ArrayList<Point> candidat_rest = new ArrayList<Point>();

		current = replace(fvs, reste, origine);
		
		Evaluation evaluation = new Evaluation();
		for (Point poi : reste) {
			for (Point pf : fvs) {
				if(evaluation.isneighbor(poi, pf, origine, 100)) {
					candidat.remove(pf);
					candidat_rest.add(pf);
					candidat_rest.remove(poi);
					candidat.add(poi);
					fvs = replace(candidat, candidat_rest, origine);
				}
			}}

		return current;

	}

	public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> fvs = new ArrayList<Point>();
		ArrayList<Point> reste = new ArrayList<Point>();
		for(Point p : points) {
			reste.add(p);
		}
		fvs = init(reste);
		for(Point p : fvs) {
			reste.remove(p);
		}
		return localSearch(fvs,reste,points);

		// goulton
//		reste = points;
//		Evaluation evaluation = new Evaluation();
//		while (!evaluation.isValid(reste, fvs, 100)) {
//			int max = 0;
//			int maxP = 0;
//			for (int i = 0; i < points.size(); i++) {
//
//				int population = evaluation.getPopulation(points.get(i), points, 100);
//				if (population > max) {
//					max = population;
//					maxP = i;
//				}
//			}
//			fvs.add(points.get(maxP));
//			reste.remove(points.get(maxP));
//		}
//
//		System.out.println("Size = " + fvs.size());
//		return fvs;
	}

	public static void main(String arg[]) throws FileNotFoundException {

		File file = new File("input.points");
		Scanner scanner = new Scanner(file);

		ArrayList<Point> solution = new ArrayList<Point>();
		ArrayList<Point> reste = new ArrayList<Point>();

		ArrayList<Point> origine = new ArrayList<Point>();

		while (scanner.hasNextInt()) {
			origine.add(new Point(scanner.nextInt(), scanner.nextInt()));
		}
		scanner.close();

		supportGUI.FramedGUI fram = new FramedGUI(1200, 800, null, 0, 100, false);
		DefaultTeam team = new DefaultTeam();
		solution = team.calculFVS(origine, 100);
		fram.drawPoints(origine, origine);

	}
}
