package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;

public class Genome {
	public enum Sex {
		MALE, FEMALE, STERILE
	}
	public ChromosomePair[] chromosomes;
	public final int[] LOCUS;

	public Allele[] femalea;
	public Allele[] femaleb;
	public Allele[] male;

	public Genome(Allele[][][] c) {
		LOCUS = new int[]{
			5,
			3,
			3,
			2,
		};
		chromosomes = new ChromosomePair[LOCUS.length];
		for(int i=0; i<LOCUS.length; ++i){
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
		LOCUS = new int[]{
			5,
			3,
			3,
			2,
		};
		chromosomes = c;
	}
	
	public Genome breed(Genome other) {
		ChromosomePair[] childc = new ChromosomePair[LOCUS.length]; 
		for(int i=0; i<chromosomes.length; ++i) {
			childc[i] = new ChromosomePair(chromosomes[i].meiosis(), other.chromosomes[i].meiosis());
		}
		return new Genome(childc);
	}
	
	// Master inheritance function
	public float phenotype(int i, int j, boolean mendelian, float dom, float rec, float mut){
		if(!mendelian){
			// Since the inheritance is not mendelian, we divide the inheritance targets by 2 so that
			// two genes will add up to form the whole.
			return
					((chromosomes[i].a[j] == Allele.DOM) ? dom/2
					: (chromosomes[i].a[j] == Allele.REC) ? rec/2
					: mut/2)
					+ 
					((chromosomes[i].b[j] == Allele.DOM) ? dom/2
					: (chromosomes[i].b[j] == Allele.REC) ? rec/2
					: mut/2);
		}
		return
				((chromosomes[i].a[j] == Allele.DOM || chromosomes[i].b[j] == Allele.DOM) ? dom
				: (chromosomes[i].a[j] == Allele.REC || chromosomes[i].b[j] == Allele.REC) ? rec
				: mut );
	}

	public float phenotypeSex(int j, boolean mendelian, float dom, float rec, float mut){
		if(!mendelian){
			// Since the inheritance is not mendelian, we divide the inheritance targets by 2 so that
			// two genes will add up to form the whole.
			return
					((femalea[j] == Allele.DOM) ? dom/2
					: (femalea[j] == Allele.REC) ? rec/2
					: mut/2)
					+ 
					((femaleb[j] == Allele.DOM) ? dom/2
					: (femaleb[j] == Allele.REC) ? rec/2
					: mut/2);
		}
		return
				((femalea[j] == Allele.DOM || femaleb[j] == Allele.DOM) ? dom
				: (femalea[j] == Allele.REC || femaleb[j] == Allele.REC) ? rec
				: mut );
	}


	public boolean phenotype(int i, int j, boolean homo, Allele flag){
		if(homo)
			return chromosomes[i].a[j] == flag && chromosomes[i].b[j] == flag;
		return chromosomes[i].a[j] == flag || chromosomes[i].b[j] == flag;
	}

	public float phenotypeMale(int j, float dom, float rec, float mut){
		return
				((male[j] == Allele.DOM) ? dom
				: (male[j] == Allele.REC) ? rec
				: mut);
	}
	
	public void express(Creature c){
		/*
		 * RED		legThick
		 * GREEN	width		legLength
		 */
		int i = 0;
		int j = 0;
		
		// ---- Chromosome 1
		c.color.r = phenotype(i, j, false, 1, .5f, .1f);
		j = 1;
		c.legThick = phenotype(i, j, false, 1, .8f, .5f);
		j = 2;
		c.legConcave = !phenotype(i, j, false, Allele.DOM);
		j = 3;
		c.speed = phenotype(i, j, false, 1, .4f, .1f);
		j = 4;
		c.eyeColor.b = phenotype(i, j, true, .9f, .4f, 0);

		// ---- Chromosome 2
		++i; j = 0;
		c.color.g = phenotype(i, j, false, .9f, .3f, 0);
		j = 1;
		c.width = phenotype(i, j, false, 1, .6f, .1f);
		j = 2;
		c.legLength = phenotype(i, j, false, 1, .5f, .1f);

		// ---- Chromosome 3
		++i; j = 0;
		c.eyeColor.g = phenotype(i, j, true, .2f, .8f, 0);
		j = 1;
		c.jump = phenotype(i, j, false, 1, .7f, .4f);
		j = 2;
		c.albino = phenotype(i, j, true, Allele.MUT); 
		
		
		// ---- Chromosome 4 (SEX)
		++i; j = 0;
		setupSex(i, j, c);
		j = 1;
		if(c.sex == Genome.Sex.MALE){	
			c.secondaryColor.g = phenotypeMale(j, .6f, .3f, .1f);
		}
		else if(c.sex == Genome.Sex.FEMALE){
			femalea = chromosomes[i].a;
			femaleb = chromosomes[i].b;
			c.secondaryColor.r = phenotypeSex(j, true, .9f, .4f, .1f);
		}
	}

	public void setupSex(int i, int j, Creature c){
		Allele ca = chromosomes[i].a[j];
		Allele cb = chromosomes[i].b[j];
		if(ca == cb && ca == Allele.FEMALE)
			c.sex = Genome.Sex.FEMALE;
		else if((ca == Allele.MALE && cb == Allele.FEMALE) || (ca == Allele.FEMALE && cb == Allele.MALE))
			c.sex = Genome.Sex.MALE;
		else
			c.sex = Genome.Sex.STERILE;

		j = 1;
		if(c.sex == Genome.Sex.MALE){
			femalea = (chromosomes[i].a[j] == Allele.FEMALE) ? chromosomes[i].a : chromosomes[i].b;
			femaleb = femalea;
			male = (chromosomes[i].a[j] == Allele.MALE) ? chromosomes[i].a : chromosomes[i].b;
		}
	}
	

	public String toString() {
		String ret = "Genome{";
		for(ChromosomePair p : chromosomes)
			ret += p;
		return ret + "}\n";
	}
}
