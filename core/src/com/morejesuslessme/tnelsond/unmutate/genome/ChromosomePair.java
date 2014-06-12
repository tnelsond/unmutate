package com.morejesuslessme.tnelsond.unmutate.genome;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.MathUtils;

public class ChromosomePair {
	public Allele[] a;
	public Allele[] b;

	public ChromosomePair(Allele[] a, Allele[] b) {
		this.a = a;
		this.b = b;
	}

	// Only for reflection
	public ChromosomePair(){
		a = null;
		b = null;
	}

	public Allele[] meiosis(float mutation, float crossover) {
		Allele[] chromosome = new Allele[a.length];
		boolean cAllele = MathUtils.randomBoolean();
		for(int i=0; i<a.length; ++i) {
			chromosome[i] = cAllele ? a[i] : b[i];
			if(MathUtils.randomBoolean(mutation)){
				System.out.println("MUTATION");
				chromosome[i] = Allele.MUT;
			}
			if(MathUtils.randomBoolean(crossover) && a[0] == Allele.MALE && b[0] != Allele.MALE)
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
