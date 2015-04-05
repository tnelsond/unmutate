package com.morejesuslessme.tnelsond.unmutate.genome;

import com.morejesuslessme.tnelsond.unmutate.*;

public class Genome00 extends Genome{
	public void initLOCUS(){
		LOCUS = new int[]{
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
			{ {Allele.DOM, Allele.DOM}},
			{ {Allele.REC, Allele.DOM}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.FEMALE, Allele.FEMALE}}})
		:
		(new Allele[][][] {
			{ {Allele.REC, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.MALE, Allele.FEMALE}}})
		);
	}

	public void express(Creature c){
		c.legLength *= .5;
		c.width *= .8;
		c.color.r = 0.4f;
		c.eyeColor.b = 0.3f;
		c.eyeColor.r = 0.1f;
		int i = 0;
		int j = 0;
		// Chromosome 1
		c.color.b = phenotype(i, j, false, 0.9f, 0.0f, 0);
		// Chromosome 2
		++i; j = 0;
		c.legLength = phenotype(i, j, false, .6f, .3f, .1f);
		// Chromosome 3
		++i; j = 0;
		c.eyeColor.g = phenotype(i, j, true, .7f, .3f, 0);
		// Sex Chromosome (4)
		++i; j = 0;
		setupSex(i, j, c);
	}
}
