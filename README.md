# MHAP with k-mer validation
These files were used in the ms "Improved assembly of noisy long reads by k-mer validation" (Carvalho, Dupim, Nassar, Genome Research, in revision).
The ms describes a modification of the MHAP alghoritm, which implements k-mer validation. 
The original MHAP code is described in Berlin et al 2015. Assembling large genomes with single-molecule sequencing and locality-sensitive hashing. Nature Biotechnology 33(6): 623-630


## Build

You must have a recent  [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html "JDK") and [Apache ANT](http://ant.apache.org/ "ANT") available. To checkout and build run:

    git clone https://github.com/bernardo1963/kmer_validation.git
    cd code/MHAP-1.5v6
    ant
    
For a quick test:

    cd target
    java -Xmx3g -server -jar mhap-1.5v6.jar -s <the fasta file to be used> -q <the fasta file to be compared> --valid-kmers <the file of the valid kmers used as filter to remove error and repetitive k-mers>

## Files

The folder "assemblies" contain the PacBio assemblies of D. melanogaster, C. elegans, and A. thaliana using the three assembly methods (standard MHAP, k-mer validation with low frequency masking, and k-mer validation with low and high frequency masking).

The folder "code"  contains the modified MHAP and some additional files. The original MHAP code is described in Berlin et al 2015. Assembling large genomes with single-molecule sequencing and locality-sensitive hashing. Nature Biotechnology 33(6): 623-630

The folder code/sequences contains the files that can be used for the quick test as follows: 

    java -Xmx3g -server -jar mhap-1.5v6.jar -s ../../sequences/fragment_A.fasta -q ../../sequences/fragment_B.fasta --valid-kmers ../../sequences/valid_kmers.kmer2

 
## Running MHAP / PBcR with k-mer validation.

The original MHAP overlapper was described in Berlin K, et al. (2015). 

We modified several files of the source code of version mhap-1.5b1 , which is used in the Celera Assembler 8.3rc2 . We named the modified version mhap-1.5v6 .

The compiled file mhap-1.5v6.jar  should be copied to the same directory of the original file mhap-1.5b1.jar (e.g., /home3/users/bernardo/programs/wgs-8.3rc2/Linux-amd64/lib/java/ ).

Besides the files required by standard MHAP (version 1.5b1), the modified MHAP requires two additional jar files: lucene-core-4.9.1.jar (which implements the BitSet) and commons-compress-1.9.jar .  These files should be placed at the same directory of the other auxiliary jar files (guava-18.0.jar , etc).

The modified script PBcR_v6  should be copied to the same directory of the original file  PBcR (e.g., /home3/users/bernardo/programs/wgs-8.3rc2/Linux-amd64/bin/ ).

In order to run mhap-1.5v6 (either alone or within the PBcR_v6 script) with k-mer validation, it is also necessary a file with the list of valid k-mers , which  can be generated as follows:
1) download the Illumina fastq files (e.g., the Drosophila ISO-1 Illumina reads).
2) Run jellyfish count as follows:
   zcat 2057_*.fastq.gz | jellyfish count -Q "+"  -m 16 -s 10G  -t 8 -o Illumina_q10_C.jelly -C  /dev/fd/0
3) Run jellyfish dump as follows:
   jellyfish dump -c -L 13 -U 150 Illumina_q10_C.jelly  | awk '(1==1){print $1}' > dros_L13_U150.kmer  #for LH-masking
   jellyfish dump -c -L 13        Illumina_q10_C.jelly  | awk '(1==1){print $1}' > dros_L13.kmer       #for L-masking


A typical command line for  running mhap-1.5v6 alone:

java -Xmx48g -server -jar  mhap-1.5v6.jar -s melR6_frag_A.PB.fasta  -q melR6_frag_B.PB.fasta  --no-self --num-hashes 512 --num-min-matches 3 --threshold 0.04  --weighted --valid-kmers dros_L13_U150.kmer

If run without the  --valid-kmers option the modified MHAP produces an output which is identical to the original MHAP code.
    

A typical command line for assembly with k-mer validation is:

PBcR_v6  -length 500  -l dros_LH   -s ~/mhap_v6.spec  valid_kmer_file=dros_L13_U150.kmer  -fastq ./dmel_filtered.fastq  genomeSize=180000000 > dros_LH.out 2>&1      
      

 The spec file mhap_v6.spec follows:

#mhap_v6.spec  based on UFSCAR_v5.spec   generic spec for running PBcR_v6 in a 24-core machine with 144 Gb RAM.  
ovlMemory=96
merylMemory=96000
ovlStoreMemory=96000
cnsConcurrency=24
consensusConcurrency=24
threads=24
assemble=1
javaPath=/home3/users/bernardo/programs/jdk1.8.0_60/bin
frgCorrThreads=4
frgCorrConcurrency=6
mhap_jar=/home3/users/bernardo/programs/wgs-8.3rc2/Linux-amd64/lib/java/mhap-1.5v6.jar
mhap=-k 16 --num-hashes 512 --num-min-matches 3 --threshold 0.04 --filter-threshold 0.000005 --weighted



