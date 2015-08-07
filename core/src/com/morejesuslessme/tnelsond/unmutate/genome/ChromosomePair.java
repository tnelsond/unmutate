package com.morejesuslessme.tnelsond.unmutate.genome;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.MathUtils;

public class ChromosomePair {
	public int[] a;
	public int[] b;

	public ChromosomePair(int[] a, int[] b) {
		this.a = a;
		this.b = b;
	}

	// Only for reflection
	public ChromosomePair(){
		a = null;
		b = null;
	}

	public int[] meiosis(float mutation, float crossover) {
		int[] chromosome = new int[a.length];
		int crossover_a = -1;
		int crossover_b = -1;
		boolean anycrossover = MathUtils.randomBoolean(); // Only 50% of chromosomes have crossover in diploids.
		if(anycrossover){
			if(MathUtils.randomBoolean(crossover)){
				crossover_a = MathUtils.random(a.length);
			}
			if(MathUtils.randomBoolean(crossover)){
				crossover_b = MathUtils.random(a.length);
			}
		}
		boolean cAllele = MathUtils.randomBoolean();
		for(int i=0; i<a.length; ++i) {
			chromosome[i] = cAllele ? a[i] : b[i];
			if(MathUtils.randomBoolean(mutation)){
				System.out.println("MUTATION");
				chromosome[i] = Allele.MUT;
			}
			if(i == crossover_a || i == crossover_b)
				cAllele = !cAllele;
		}
		return chromosome;
	}

	public String toString() {
		String ret = "{";
		for(int i = 0; i < a.length; ++i){
			ret += "[" + a[i] + " " + b[i] + "],";
		}
		return ret + "},";
	}
}
