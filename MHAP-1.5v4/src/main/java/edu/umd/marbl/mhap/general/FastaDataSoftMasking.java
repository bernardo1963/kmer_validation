package edu.umd.marbl.mhap.general;

import java.io.IOException;

import edu.umd.marbl.mhap.utils.MhapRuntimeException;

public class FastaDataSoftMasking extends FastaData 
{
	public FastaDataSoftMasking(String file, int offset) throws IOException 
	{
		super(file, offset);
	}
	
	@Override
	protected boolean enqueueNextSequenceInFile() throws IOException
	{
		synchronized (this.fileReader)
		{
			if (this.readFullFile)
				return false;

			// try to read the next line
			if (this.lastLine == null)
			{
				this.lastLine = this.fileReader.readLine();

				// there is no next line
				if (this.lastLine == null)
				{
					this.fileReader.close();
					this.readFullFile = true;
					return false;
				}
			}

			// process the header
			if (!this.lastLine.startsWith(">"))
				throw new MhapRuntimeException("Next sequence does not start with >. Invalid format.");

			// process the current header
			String header = null;
			if (SequenceId.STORE_FULL_ID)
				header = this.lastLine.substring(1).split("[\\s,]+", 2)[0];
			
			//read the first line of the sequence
			this.lastLine = this.fileReader.readLine();

			StringBuilder fastaSeq = new StringBuilder();
			while (true)
			{
				if (this.lastLine == null || this.lastLine.startsWith(">"))
				{
					//generate sequence id
					SequenceId id;
					if (SequenceId.STORE_FULL_ID)
						id = new SequenceId(this.numberProcessed.intValue() + this.offset + 1, true, header);
					else
						id = new SequenceId(this.numberProcessed.intValue() + this.offset + 1);

					Sequence seq = new Sequence(fastaSeq.toString(), id);

					// enqueue sequence
					this.sequenceList.add(seq);
					this.numberProcessed.getAndIncrement();

					if (this.lastLine == null)
					{
						this.fileReader.close();
						this.readFullFile = true;
					}

					return true;
				}

				// append the last line
				fastaSeq.append(this.lastLine);
				this.lastLine = this.fileReader.readLine();
			}
		}

	}

}
