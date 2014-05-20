package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.MathUtils;

public class ChromosomePair {
	public Allele[] a;
	public Allele[] b;
	public static float MUTATION = 0.04f;
	public static float CROSSOVER = 0.2f;
	public ChromosomePair(Allele[] a, Allele[] b) {
		this.a = a;
		this.b = b;
	}
	public Allele[] meiosis() {
		Allele[] chromosome = new Allele[a.length];
		boolean cAllele = MathUtils.randomBoolean();
		for(int i=0; i<a.length; ++i) {
			chromosome[i] = cAllele ? a[i] : b[i];
			if(MathUtils.randomBoolean(ChromosomePair.MUTATION)){
				System.out.println("MUTATION");
				chromosome[i] = Allele.MUT;
			}
			if(MathUtils.randomBoolean(ChromosomePair.CROSSOVER) && a[0] == Allele.MALE && b[0] != Allele.MALE)
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
