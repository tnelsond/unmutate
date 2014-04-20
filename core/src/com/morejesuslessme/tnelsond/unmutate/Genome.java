package com.morejesuslessme.tnelsond.unmutate;

public class Genome {
	public enum Sex {
		MALE, FEMALE, STERILE
	}
	public ChromosomePair[] chromosomes;
	static public int[] LOCUS = {
		4,
		3,
		3,
	};
	
	public Genome(Allele[][][] c) {
		chromosomes = new ChromosomePair[Genome.LOCUS.length];
		for(int i=0; i<Genome.LOCUS.length; ++i){
			int size = c[i].length;
			Allele[] tempa = new Allele[size];
			Allele[] tempb = new Allele[size];
			for(int j=0; j<size; ++j) {
				tempa[j] = c[i][j][0];
				tempb[j] = c[i][j][1];
			}
			chromosomes[i] = new ChromosomePair(tempa, tempb);
		}
	}
	
	public Genome(ChromosomePair[] c) {
		chromosomes = c;
	}
	
	public Genome breed(Genome other) {
		ChromosomePair[] childc = new ChromosomePair[Genome.LOCUS.length]; 
		for(int i=0; i<chromosomes.length; ++i) {
			childc[i] = new ChromosomePair(chromosomes[i].meiosis(), other.chromosomes[i].meiosis());
		}
		return new Genome(childc);
	}
}