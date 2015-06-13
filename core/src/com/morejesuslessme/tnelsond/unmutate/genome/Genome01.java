package com.morejesuslessme.tnelsond.unmutate.genome;

import com.morejesuslessme.tnelsond.unmutate.*;

public class Genome01 extends Genome{
	public void initLOCUS(){
		LOCUS = new int[]{
			4,
			2,
			3,
			4
		};
		MUTATION = 0.00f;
	}

	public Genome01(ChromosomePair[] c){
		super(c);
	}
	
	public Genome01(){
		super();
	}

	public Genome01(boolean female){
		super(female ?
		(new Allele[][][] {
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.FEMALE, Allele.FEMALE},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}}})
		:
		(new Allele[][][] {
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.DOM, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}},
			{{Allele.MALE, Allele.FEMALE},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM},
			 {Allele.REC, Allele.DOM}}})
		);
	}

	public void express(Creature c){
		int i = 0;
		int j = 0;
		c.legConcave = true;
		// Chromosome 1
		c.legLength = phenotype(i, j, false, 0.5f, 0.3f, 0);
		++j;
		c.eyeColor.r = phenotype(i, j, true, 0.2f, 0, 0);
		++j;
		c.speed = phenotype(i, j, true, 1.5f, .6f, .1f);
		++j;
		c.color.b = phenotype(i, j, true, 0.3f, 0.2f, 0.1f);
		// Chromosome 2
		++i; j = 0;
		c.hipWidth = phenotype(i, j, false, .75f, .2f, 1);
		++j;
		c.legConcave = phenotype(i, j, false, Allele.DOM);
		// Chromosome 3
		++i; j = 0;
		c.eyeColor.g = phenotype(i, j, true, .5f, .2f, 0);
		++j;
		c.legLength = phenotype(i, j, false, .3f, .5f, .1f);
		++j;
		c.color.g = phenotype(i, j, true, 0.4f, 0.1f, 0.1f);
		// Sex Chromosome (4)
		++i; j = 0;
		setupSex(i, j, c);
		++j;
		if(c.sex == Genome.Sex.MALE){
			c.secondaryColor.b = phenotypeMale(j, 0.8f, .1f, .1f);
		}
		if(c.sex == Genome.Sex.FEMALE){
			c.secondaryColor.r = phenotypeSex(j, false, 0.6f, 0f, 0f);
		}
		++j;
		c.eyeColor.b = phenotypeSex(j, false, 0.4f, 0.2f, 0);
		c.eyeColor.g -= c.eyeColor.b;
		++j;
		c.color.r = phenotypeSex(j, true, 0.2f, 0.0f, 0.4f);
	}
}
