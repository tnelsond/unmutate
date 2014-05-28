package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;

public class Genome {
	public enum Sex {
		MALE, FEMALE, STERILE
	}
	public ChromosomePair[] chromosomes;
	static public int[] LOCUS = {
		5,
		3,
		3,
		2,
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
	
	public void express(Creature c){
		/*
		 * RED		legThick
		 * GREEN	width		legLength
		 */
		int i = 0;
		int j = 0;
		
		// ---- Chromosome 1
		c.color.r = ((chromosomes[i].a[j] == Allele.DOM) ? .5f : ((chromosomes[i].a[j] == Allele.REC) ? .25f : 0)) + ((chromosomes[i].b[j] == Allele.DOM) ? .5f : ((chromosomes[i].b[j] == Allele.REC) ? .25f : 0));
		j = 1;
		c.legThick = ((chromosomes[i].a[j] == Allele.DOM) ? 5 : ((chromosomes[i].a[j] == Allele.REC) ? 4 : 2)) + ((chromosomes[i].b[j] == Allele.DOM) ? 5 : ((chromosomes[i].b[j] == Allele.REC) ? 4 : 2));
		j = 2;
		c.legConcave = chromosomes[i].a[j] != Allele.DOM && chromosomes[i].b[j] != Allele.DOM;
		j = 3;
		c.speed *= ((chromosomes[i].a[j] == Allele.DOM) ? 3 : ((chromosomes[i].a[j] == Allele.REC) ? 1 : .1)) + ((chromosomes[i].b[j] == Allele.DOM) ? 3 : ((chromosomes[i].b[j] == Allele.REC) ? 1 : .1));
		j = 4;
		c.eyeColor.b = Math.max((chromosomes[i].a[j] == Allele.DOM) ? .9f : ((chromosomes[i].a[j] == Allele.REC) ? .4f : 0), ((chromosomes[i].b[j] == Allele.DOM) ? .9f : ((chromosomes[i].b[j] == Allele.REC) ? .4f : 0)));
		
		// ---- Chromosome 2
		++i; j = 0;
		c.color.g = ((chromosomes[i].a[j] == Allele.DOM) ? .4f : ((chromosomes[i].a[j] == Allele.REC) ? .1f : 0)) + ((chromosomes[i].b[j] == Allele.DOM) ? .4f : ((chromosomes[i].b[j] == Allele.REC) ? .1f : 0));
		j = 1;
		c.width *= ((chromosomes[i].a[j] == Allele.DOM) ? .5 : ((chromosomes[i].a[j] == Allele.REC) ? .2 : .1)) + 
				((chromosomes[i].b[j] == Allele.DOM) ? .5 : ((chromosomes[i].b[j] == Allele.REC) ? .2 : .1));
		j = 2;
		c.legLength *= ((chromosomes[i].a[j] == Allele.DOM) ? .5 : ((chromosomes[i].a[j] == Allele.REC) ? .2 : .1)) +
				((chromosomes[i].b[j] == Allele.DOM) ? .5 : ((chromosomes[i].b[j] == Allele.REC) ? .2 : .1));
		
		// ---- Chromosome 3
		++i; j = 0;
		c.eyeColor.g = ((chromosomes[i].a[j]== Allele.DOM) ? .4f : ((chromosomes[i].a[j] == Allele.REC) ? .25f : 0)) + ((chromosomes[i].b[j] == Allele.DOM) ? .4f : ((chromosomes[i].b[j] == Allele.REC) ? .25f : 0));
		j = 1;
		c.jump += ((chromosomes[i].a[j]== Allele.DOM) ? 6 : ((chromosomes[i].a[j] == Allele.REC) ? 2 : 0))
				+ ((chromosomes[i].b[j] == Allele.DOM) ? 6 : ((chromosomes[i].b[j] == Allele.REC) ? 2 : 0));
		j = 2;
		c.albino = (chromosomes[i].a[j] == Allele.MUT && chromosomes[i].b[j] == Allele.MUT);
		
			// Make colors subtractive
		Color tempcolor = new Color(.9f, .9f, .9f, 1);
		if(!c.albino) {
			c.color = tempcolor.cpy().sub(c.color.b + c.color.g, c.color.r + c.color.b, c.color.g + c.color.r, 0);
			c.eyeColor = tempcolor.cpy().sub(c.eyeColor.b + c.eyeColor.g, c.eyeColor.r + c.eyeColor.b, c.eyeColor.g + c.eyeColor.r, 0);
		}
		else {
			c.color = tempcolor.cpy();
			c.eyeColor = new Color(1, 0, 0, 1);
		}

		// ---- Chromosome 4 (SEX)
		++i; j = 0;
		Allele ca = chromosomes[i].a[j];
		Allele cb = chromosomes[i].b[j];
		if(ca == cb && ca == Allele.FEMALE)
			c.sex = Genome.Sex.FEMALE;
		else if((ca == Allele.MALE && cb == Allele.FEMALE) || (ca == Allele.FEMALE && cb == Allele.MALE))
			c.sex = Genome.Sex.MALE;
		else
			c.sex = Genome.Sex.STERILE;

		j = 1;
		Allele[] femalea = null, femaleb = null, male = null;
		if(c.sex == Genome.Sex.MALE){
			femalea = (chromosomes[i].a[j] == Allele.FEMALE) ? chromosomes[i].a : chromosomes[i].b;
			femaleb = femalea;
			male = (chromosomes[i].a[j] == Allele.MALE) ? chromosomes[i].a : chromosomes[i].b;
			// Male inheritance here
			c.secondaryColor.g = ((male[j] == Allele.DOM) ? .9f : (male[j] == Allele.REC) ? .4f : .1f);
		}
		else if(c.sex == Genome.Sex.FEMALE){
			femalea = chromosomes[i].a;
			femaleb = chromosomes[i].b;
			c.secondaryColor.r = ((femalea[j] == Allele.DOM) ? .45f : (femalea[j] == Allele.REC) ? .2f : .05f) + ((femaleb[j] == Allele.DOM) ? .45f : (femaleb[j] == Allele.REC) ? .2f : .05f);
		}

			tempcolor = new Color(1f, .9f, .9f, 1);
			c.secondaryColor = tempcolor.cpy().sub(c.secondaryColor.b + c.secondaryColor.g, c.secondaryColor.r + c.secondaryColor.b, c.secondaryColor.g + c.secondaryColor.r, 0);
	}

	public String toString() {
		String ret = "Genome{";
		for(ChromosomePair p : chromosomes)
			ret += p;
		return ret + "}\n";
	}
}
