package com.morejesuslessme.tnelsond.unmutate.genome;

import com.morejesuslessme.tnelsond.unmutate.*;

public class Genome00 extends Genome{
	public void initLOCUS(){
		LOCUS = new int[]{
			1,
			1,
			1,
			1,
			1
		};
		MUTATION = 0;
	}

	public Genome00(ChromosomePair[] c){
		super(c);
	}
	
	public Genome00(){
		super();
	}

	public Genome00(boolean female){
		super(female ?
		(new Allele[][][] {
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.DOM}},
			{ {Allele.REC, Allele.REC}},
			{ {Allele.FEMALE, Allele.FEMALE}}})
		:
		(new Allele[][][] {
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.REC, Allele.REC}},
			{ {Allele.DOM, Allele.DOM}},
			{ {Allele.MALE, Allele.FEMALE}}})
		);
	}

	public void express(Creature c){
		c.legLength *= .5;
		c.width *= .8;
		c.color.r = 0.4f;
		int i = 0;
		int j = 0;
		// Chromosome 1
		c.color.b = phenotype(i, j, true, 1, 0, 0);
		// Chromosome 2
		++i; j = 0;
		c.legLength = phenotype(i, j, true, .6f, .3f, .1f);
		// Chromosome 3
		++i; j = 0;
		c.eyeColor.g = phenotype(i, j, true, .4f, .1f, 0);
		// Chromosome 4
		++i; j = 0;
		c.eyeColor.b = phenotype(i, j, true, .5f, .1f, 0);
		// Sex Chromosome (5)
		++i; j = 0;
		setupSex(i, j, c);
	}
}
