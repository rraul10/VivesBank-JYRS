package jyrs.dev.vivesbank.users.clients.mappers;

import jyrs.dev.vivesbank.users.clients.dto.AddressDto;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {


    public Client toClientCreate(ClientRequestCreate clientRequestCreate) {
        if (clientRequestCreate == null) {
            return null;
        }

        return Client.builder()
                .dni(clientRequestCreate.getDni())
                .nombre(clientRequestCreate.getNombre())
                .apellidos(clientRequestCreate.getApellidos())
                .fotoDni(clientRequestCreate.getFotoDni())
                .numTelefono(clientRequestCreate.getNumTelefono())
                .email(clientRequestCreate.getEmail())
                .direccion(toAdress(clientRequestCreate.getDireccion()))
                .build();

    }

    public Client toClientUpdate(ClientRequestUpdate clientRequestUpdate) {
        if (clientRequestUpdate == null) {
            return null;
        }

        return Client.builder()
                .nombre(clientRequestUpdate.getNombre())
                .apellidos(clientRequestUpdate.getApellidos())
                .fotoDni(clientRequestUpdate.getFotoDni())
                .numTelefono(clientRequestUpdate.getNumTelefono())
                .email(clientRequestUpdate.getEmail())
                .direccion(toAdress(clientRequestUpdate.getDireccion()))
                .build();

    }

    public ClientResponse toResponse(Client client) {
        if (client == null) {
            return null;
        }

        return ClientResponse.builder()
                .dni(client.getDni())
                .nombre(client.getNombre())
                .apellidos(client.getApellidos())
                .numTelefono(client.getNumTelefono())
                .email(client.getEmail())
                .direccion(toAddresDto(client.getDireccion()))
                .cuentas(client.getCuentas())
                .build();
    }

    private Address toAdress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }

        return Address.builder()
                .calle(addressDto.getCalle())
                .numero(addressDto.getNumero())
                .ciudad(addressDto.getCiudad())
                .provincia(addressDto.getProvincia())
                .pais(addressDto.getPais())
                .cp(addressDto.getCp())
                .build();
    }

    private AddressDto toAddresDto(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressDto(
                address.getCalle(),
                address.getNumero(),
                address.getCiudad(),
                address.getProvincia(),
                address.getPais(),
                address.getCp()
        );
    }


}
