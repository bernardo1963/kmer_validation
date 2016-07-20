# MHAP

MinHash alignment process (MHAP pronounced MAP): locality sensitive hashing to detect overlaps and utilities. This is an optimization of the algorithm where it can receive a file with valid kmers to make the process of validation faster.

## Build

You must have a recent  [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html "JDK") and [Apache ANT](http://ant.apache.org/ "ANT") available. To checkout and build run:

    git clone https://github.com/csmartins/kmer_validation.git
    cd MHAP-1.5v6
    ant
    
For a quick run:

    cd target
    java -Xmx3g -server -jar mhap-1.5v6.jar -s <the fasta file to be used> -q <the fasta file to be compared> --valid-kmers <the file of the valid kmers to be used as filter for the hashes>

## Files

The sequences directory contains the files that can be used as a test with the java command above. The "fragment_A.fasta" file is to be used with the -s option, the "fragment_B.fasta" is to be used with the -q option and the "valid_kmers.kmer2" is to bem used with the --valid-kmers option.

Using those files the command to run MHAP would be:

    java -Xmx3g -server -jar mhap-1.5v6.jar -s ../../sequences/fragment_A.fasta -q ../../sequences/fragment_B.fasta --valid-kmers ../../sequences/valid_kmers.kmer2
