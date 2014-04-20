package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.utils.TimeUtils;

public class ChromosomePair {
	public Allele[] a;
	public Allele[] b;
	public static float MUTATION = 0.02f;
	public static float CROSSOVER = 0.2f;
	public ChromosomePair(Allele[] a, Allele[] b) {
		this.a = a;
		this.b = b;
	}
	public Allele[] meiosis() {
		TRandom.r.setSeed(TimeUtils.nanoTime());
		Allele[] chromosome = new Allele[a.length];
		boolean cAllele = TRandom.r.nextBoolean();
		for(int i=0; i<a.length; ++i) {
			chromosome[i] = cAllele ? a[i] : b[i];
			if(TRandom.r.nextGaussian() <= ChromosomePair.MUTATION)
				chromosome[i] = Allele.MUT;
			if(TRandom.r.nextGaussian() <= ChromosomePair.CROSSOVER)
				cAllele = !cAllele;
		}
		return chromosome;
	}
}
