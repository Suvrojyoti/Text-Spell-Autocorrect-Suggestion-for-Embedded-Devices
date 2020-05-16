#include <stdio.h>
#include <string.h>
#include <math.h>
#define MAXN 90000 
#define LEN 30

int main()
{
    unsigned short int end=1;
    FILE *fp = fopen("BKfile", "w+");
    fseek(fp, 0, SEEK_SET);
    fseek(fp, MAXN*(sizeof(int)*2*LEN + 30), SEEK_SET);
    end=0;
    fwrite(&end, sizeof(int), 1, fp);
    fclose(fp);


    FILE *rtfp = fopen("RTfile", "w+");
    fseek(rtfp, 0, SEEK_SET);
    fseek(rtfp, 1*(sizeof(int)*2*LEN + 30), SEEK_SET);
    end=0;
    fwrite(&end, sizeof(int), 1, rtfp);
    fclose(rtfp);
    return 0;
}