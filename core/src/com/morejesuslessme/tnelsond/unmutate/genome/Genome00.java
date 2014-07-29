package com.morejesuslessme.tnelsond.unmutate.genome;

import com.morejesuslessme.tnelsond.unmutate.*;

public class Genome00 extends Genome{
	public void initLOCUS(){
		LOCUS = new int[]{
			1,
			1,
		};
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
			{ {Allele.FEMALE, Allele.FEMALE}}})
		:
		(new Allele[][][] {
			{ {Allele.DOM, Allele.REC}},
			{ {Allele.MALE, Allele.FEMALE}}})
		);
	}

	public void express(Creature c){
		c.width *= .8;
		c.color.r = 0.4f;
		c.eyeColor.b = 0.6f;
		c.eyeColor.g = 0.2f;
		int i = 0;
		int j = 0;
		// Chromosome 1
		c.legLength *= phenotype(i, j, false, 1, .25f, .25f);
		// Chromosome 2
		++i; j = 0;
		setupSex(i, j, c);
	}
}
