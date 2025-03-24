package Worker.api.dto;

public class StatusDTO
{
    public StatusDTO()
    {

    }

    public StatusDTO(int progress)
    {
        this.progress=progress;

    }

    public int progress;


    public int getProgress() { return progress; }


    public void setProgress(int progress) { this.progress = progress; }
}