package commands.abstracts;

public record CommandParam(String name, String desc)
{
    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
