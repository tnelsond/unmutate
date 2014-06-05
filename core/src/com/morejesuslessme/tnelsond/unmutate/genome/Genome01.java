package com.morejesuslessme.tnelsond.unmutate.genome;

import com.morejesuslessme.tnelsond.unmutate.*;

public class Genome01 extends Genome{
	public void initLOCUS(){
		LOCUS = new int[]{
			1,
			1,
			1,
			1
		};
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
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.FEMALE, Allele.FEMALE}}})
		:
		(new Allele[][][] {
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.MALE, Allele.FEMALE}}})
		);
	}

	public void express(Creature c){
		c.legLength *= .5;
		c.width *= .8;
		c.color.r = 0.4f;
		c.eyeColor.b = 0.6f;
		c.eyeColor.g = 0.2f;
		int i = 0;
		int j = 0;
		// Chromosome 1
		c.color.b = phenotype(i, j, true, 1, 0, 0);
		// Chromosome 2
		++i; j = 0;
		c.legLength = phenotype(i, j, true, .6f, .3f, .1f);
		// Chromosome 3
		++i; j = 0;
		c.eyeColor.g = phenotype(i, j, true, .7f, .3f, .1f);
		// Sex Chromosome (4)
		++i; j = 0;
		setupSex(i, j, c);
	}
}
