module org.retropipes.dynamicdungeon {
    requires org.retropipes.diane.asset.ogg;
    requires org.retropipes.diane.asset.sound;
    requires org.retropipes.diane.fileio;
    requires org.retropipes.diane.fileio.utility;

    uses javax.sound.sampled.spi.AudioFileReader;
    uses javax.sound.sampled.spi.FormatConversionProvider;
}